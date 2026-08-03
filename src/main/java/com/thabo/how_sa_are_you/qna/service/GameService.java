package com.thabo.how_sa_are_you.qna.service;

import com.thabo.how_sa_are_you.qna.dto.*;
import com.thabo.how_sa_are_you.qna.entity.*;
import com.thabo.how_sa_are_you.qna.repository.*;
import com.thabo.how_sa_are_you.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class GameService {

    private static final int QUESTIONS_PER_ATTEMPT = 5;
    private static final long TIME_LIMIT_MS = 30_000;
    private static final long HARD_CUTOFF_MS = 60_000; // generous buffer for network/clock drift

    private final QuestionRepository questionRepository;
    private final AttemptRepository attemptRepository;
    private final AttemptAnswerRepository attemptAnswerRepository;
    private final OptionRepository optionRepository;

    public GameService(
            QuestionRepository questionRepository,
            AttemptRepository attemptRepository,
            AttemptAnswerRepository attemptAnswerRepository,
            OptionRepository optionRepository) {
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
        this.attemptAnswerRepository = attemptAnswerRepository;
        this.optionRepository = optionRepository;
    }

    @Transactional
    public StartAttemptResponse startAttempt(User user) {
        List<Question> pool = questionRepository.findByActiveTrue();

        if (pool.size() < QUESTIONS_PER_ATTEMPT) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Not enough active questions to start a game");
        }

        Collections.shuffle(pool);
        List<Question> selected = pool.subList(0, QUESTIONS_PER_ATTEMPT);

        Attempt attempt = new Attempt();
        attempt.setUser(user);
        attemptRepository.save(attempt);

        List<QuestionDto> questionDtos = selected.stream()
                .map(this::toQuestionDto)
                .toList();

        return new StartAttemptResponse(
                attempt.getId(),
                questionDtos,
                attempt.getStartedAt(),
                (int) (TIME_LIMIT_MS / 1000));
    }

    @Transactional
    public AttemptResultResponse submitAttempt(User user, UUID attemptId, SubmitAttemptRequest request) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt not found"));

        if (!attempt.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This attempt doesn't belong to you");
        }
        if (attempt.getCompletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Attempt already submitted");
        }

        LocalDateTime now = LocalDateTime.now();
        long elapsedMs = Duration.between(attempt.getStartedAt(), now).toMillis();

        if (elapsedMs > HARD_CUTOFF_MS) {
            throw new ResponseStatusException(
                    HttpStatus.GONE, "Attempt expired — too much time has passed since it started");
        }

        int score = 0;
        List<AnswerResultDto> results = new ArrayList<>();

        for (AnswerSubmission answer : request.answers()) {
            Question question = questionRepository.findById(answer.questionId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Unknown question in submission"));

            Option correctOption = question.getOptions().stream()
                    .filter(Option::isCorrect)
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR, "Question has no correct option configured"));

            Option selectedOption = null;
            boolean correct = false;

            if (answer.selectedOptionId() != null) {
                selectedOption = optionRepository.findById(answer.selectedOptionId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Unknown option in submission"));
                correct = selectedOption.getId().equals(correctOption.getId());
            }

            if (correct)
                score++;

            AttemptAnswer attemptAnswer = new AttemptAnswer();
            attemptAnswer.setAttempt(attempt);
            attemptAnswer.setQuestion(question);
            attemptAnswer.setSelectedOption(selectedOption);
            attemptAnswer.setCorrect(correct);
            attemptAnswerRepository.save(attemptAnswer);

            results.add(new AnswerResultDto(
                    question.getId(),
                    selectedOption != null ? selectedOption.getId() : null,
                    correctOption.getId(),
                    correct));
        }

        attempt.setScore(score);
        attempt.setCompletedAt(now);
        attemptRepository.save(attempt);

        return new AttemptResultResponse(attempt.getId(), score, request.answers().size(), elapsedMs, results);
    }

    private QuestionDto toQuestionDto(Question q) {
        List<OptionDto> opts = q.getOptions().stream()
                .map(o -> new OptionDto(o.getId(), o.getText()))
                .toList();
        return new QuestionDto(q.getId(), q.getText(), q.getCategory(), opts);
    }
}