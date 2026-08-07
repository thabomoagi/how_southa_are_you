package com.thabo.howsouthaareyou.qna.service;

import com.thabo.howsouthaareyou.common.exception.BadRequestException;
import com.thabo.howsouthaareyou.common.exception.NotFoundException;
import com.thabo.howsouthaareyou.common.exception.UnauthorizedException;
import com.thabo.howsouthaareyou.qna.dto.AnswerResultDto;
import com.thabo.howsouthaareyou.qna.dto.AnswerSubmission;
import com.thabo.howsouthaareyou.qna.dto.AttemptResultResponse;
import com.thabo.howsouthaareyou.qna.dto.OptionDto;
import com.thabo.howsouthaareyou.qna.dto.QuestionDto;
import com.thabo.howsouthaareyou.qna.dto.StartAttemptRequest;
import com.thabo.howsouthaareyou.qna.dto.StartAttemptResponse;
import com.thabo.howsouthaareyou.qna.dto.SubmitAttemptRequest;
import com.thabo.howsouthaareyou.qna.entity.Attempt;
import com.thabo.howsouthaareyou.qna.entity.AttemptAnswer;
import com.thabo.howsouthaareyou.qna.entity.AttemptQuestion;
import com.thabo.howsouthaareyou.qna.entity.Option;
import com.thabo.howsouthaareyou.qna.entity.Question;
import com.thabo.howsouthaareyou.qna.repository.AttemptAnswerRepository;
import com.thabo.howsouthaareyou.qna.repository.AttemptQuestionRepository;
import com.thabo.howsouthaareyou.qna.repository.AttemptRepository;
import com.thabo.howsouthaareyou.qna.repository.CategoryRepository;
import com.thabo.howsouthaareyou.qna.repository.OptionRepository;
import com.thabo.howsouthaareyou.qna.repository.QuestionRepository;
import com.thabo.howsouthaareyou.user.entity.User;
import com.thabo.howsouthaareyou.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GameService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final AttemptRepository attemptRepository;
    private final AttemptAnswerRepository attemptAnswerRepository;
    private final AttemptQuestionRepository attemptQuestionRepository;

    public StartAttemptResponse startAttempt(StartAttemptRequest request) {
        User user = getCurrentUser();

        int questionCount = request.questionCount() == null ? 10 : request.questionCount();
        int durationSeconds = request.durationSeconds() == null ? 120 : request.durationSeconds();

        List<Question> questions = selectQuestions(request, questionCount);

        if (questions.isEmpty()) {
            throw new BadRequestException("No questions available");
        }

        LocalDateTime startedAt = LocalDateTime.now();
        LocalDateTime expiresAt = startedAt.plusSeconds(durationSeconds);

        Attempt attempt = Attempt.builder()
                .user(user)
                .startedAt(startedAt)
                .expiresAt(expiresAt)
                .totalQuestions(questions.size())
                .score(0)
                .correctCount(0)
                .build();

        attemptRepository.save(attempt);

        List<AttemptQuestion> attemptQuestions = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            attemptQuestions.add(
                    AttemptQuestion.builder()
                            .attempt(attempt)
                            .question(questions.get(i))
                            .position(i)
                            .build());
        }

        attemptQuestionRepository.saveAll(attemptQuestions);

        List<QuestionDto> questionDtos = questions.stream()
                .map(this::toQuestionDto)
                .toList();

        return new StartAttemptResponse(
                attempt.getId(),
                startedAt,
                expiresAt,
                durationSeconds,
                questionDtos.size(),
                questionDtos);
    }

    public AttemptResultResponse submitAttempt(UUID attemptId, SubmitAttemptRequest request) {
        User user = getCurrentUser();

        Attempt attempt = attemptRepository.findByIdAndUserId(attemptId, user.getId())
                .orElseThrow(() -> new NotFoundException("Attempt not found"));

        if (attempt.getCompletedAt() != null) {
            return getAttemptResult(attemptId);
        }

        if (attempt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Attempt has expired");
        }

        Set<Long> allowedQuestionIds = attemptQuestionRepository
                .findByAttemptIdOrderByPositionAsc(attemptId)
                .stream()
                .map(attemptQuestion -> attemptQuestion.getQuestion().getId())
                .collect(Collectors.toSet());

        Set<Long> answeredQuestionIds = new HashSet<>();
        List<AnswerResultDto> results = new ArrayList<>();

        int score = 0;
        int correctCount = 0;

        for (AnswerSubmission submission : request.answers()) {
            if (!answeredQuestionIds.add(submission.questionId())) {
                throw new BadRequestException("Duplicate answer submitted");
            }

            if (!allowedQuestionIds.contains(submission.questionId())) {
                throw new BadRequestException("Question not part of this attempt");
            }

            Question question = questionRepository.findById(submission.questionId())
                    .orElseThrow(() -> new NotFoundException("Question not found"));

            Option selectedOption = null;
            boolean correct = false;
            int pointsEarned = 0;

            if (submission.selectedOptionId() != null) {
                selectedOption = optionRepository.findByIdAndQuestionId(
                        submission.selectedOptionId(),
                        question.getId())
                        .orElseThrow(() -> new BadRequestException("Invalid option for question"));

                correct = Boolean.TRUE.equals(selectedOption.getCorrect());
            }

            if (correct) {
                pointsEarned = calculatePoints(submission.timeTakenMs());
            }

            AttemptAnswer answer = AttemptAnswer.builder()
                    .attempt(attempt)
                    .question(question)
                    .selectedOption(selectedOption)
                    .correct(correct)
                    .timeTakenMs(submission.timeTakenMs())
                    .pointsEarned(pointsEarned)
                    .build();

            attemptAnswerRepository.save(answer);

            if (correct) {
                score += pointsEarned;
                correctCount++;
            }

            Option correctOption = findCorrectOption(question.getId());

            results.add(
                    new AnswerResultDto(
                            question.getId(),
                            selectedOption != null ? selectedOption.getId() : null,
                            correct,
                            correctOption != null ? correctOption.getId() : null,
                            correctOption != null ? correctOption.getOptionText() : null,
                            pointsEarned,
                            submission.timeTakenMs()));
        }

        attempt.setCompletedAt(LocalDateTime.now());
        attempt.setScore(score);
        attempt.setCorrectCount(correctCount);

        attemptRepository.save(attempt);

        return new AttemptResultResponse(
                attempt.getId(),
                attempt.getScore(),
                attempt.getCorrectCount(),
                attempt.getTotalQuestions(),
                attempt.getStartedAt(),
                attempt.getCompletedAt(),
                results);
    }

    public AttemptResultResponse getAttemptResult(UUID attemptId) {
        User user = getCurrentUser();

        Attempt attempt = attemptRepository.findByIdAndUserId(attemptId, user.getId())
                .orElseThrow(() -> new NotFoundException("Attempt not found"));

        if (attempt.getCompletedAt() == null) {
            throw new BadRequestException("Attempt is not completed yet");
        }

        List<AnswerResultDto> results = attemptAnswerRepository.findByAttemptId(attemptId)
                .stream()
                .sorted(Comparator.comparing(AttemptAnswer::getId))
                .map(this::toAnswerResultDto)
                .toList();

        return new AttemptResultResponse(
                attempt.getId(),
                attempt.getScore(),
                attempt.getCorrectCount(),
                attempt.getTotalQuestions(),
                attempt.getStartedAt(),
                attempt.getCompletedAt(),
                results);
    }

    private List<Question> selectQuestions(StartAttemptRequest request, int questionCount) {
        if (request.categoryId() != null && !categoryRepository.existsById(request.categoryId())) {
            throw new NotFoundException("Category not found");
        }

        List<Question> candidates = getCandidates(request, questionCount);

        if (request.difficulty() != null) {
            candidates = candidates.stream()
                    .filter(question -> question.getDifficulty() == request.difficulty())
                    .toList();
        }

        if (candidates.size() < questionCount) {
            candidates = questionRepository.findByActiveTrue();

            if (request.categoryId() != null) {
                candidates = candidates.stream()
                        .filter(question -> question.getCategory() != null)
                        .filter(question -> request.categoryId().equals(question.getCategory().getId()))
                        .toList();
            }

            if (request.difficulty() != null) {
                candidates = candidates.stream()
                        .filter(question -> question.getDifficulty() == request.difficulty())
                        .toList();
            }
        }

        List<Question> mutableCandidates = new ArrayList<>(candidates);
        Collections.shuffle(mutableCandidates);

        if (mutableCandidates.size() < questionCount) {
            throw new BadRequestException("Not enough questions available");
        }

        return mutableCandidates.subList(0, questionCount);
    }

    private List<Question> getCandidates(StartAttemptRequest request, int questionCount) {
        Pageable pageable = PageRequest.of(0, Math.max(questionCount * 10, 50));

        if (request.categoryId() != null) {
            return questionRepository.findRandomActiveQuestionsByCategoryId(request.categoryId(), pageable);
        }

        return questionRepository.findRandomActiveQuestions(pageable);
    }

    private int calculatePoints(Integer timeTakenMs) {
        if (timeTakenMs == null) {
            return 100;
        }

        if (timeTakenMs <= 3000) {
            return 150;
        }

        if (timeTakenMs <= 6000) {
            return 130;
        }

        if (timeTakenMs <= 10000) {
            return 110;
        }

        return 100;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    private QuestionDto toQuestionDto(Question question) {
        List<OptionDto> options = question.getOptions()
                .stream()
                .sorted(Comparator.comparing(Option::getId))
                .map(option -> new OptionDto(option.getId(), option.getOptionText()))
                .toList();

        return new QuestionDto(
                question.getId(),
                question.getCategory() != null ? question.getCategory().getId() : null,
                question.getPrompt(),
                question.getDifficulty(),
                options);
    }

    private AnswerResultDto toAnswerResultDto(AttemptAnswer answer) {
        Option correctOption = findCorrectOption(answer.getQuestion().getId());

        return new AnswerResultDto(
                answer.getQuestion().getId(),
                answer.getSelectedOption() != null ? answer.getSelectedOption().getId() : null,
                Boolean.TRUE.equals(answer.getCorrect()),
                correctOption != null ? correctOption.getId() : null,
                correctOption != null ? correctOption.getOptionText() : null,
                answer.getPointsEarned(),
                answer.getTimeTakenMs());
    }

    private Option findCorrectOption(Long questionId) {
        return optionRepository.findByQuestionId(questionId)
                .stream()
                .filter(option -> Boolean.TRUE.equals(option.getCorrect()))
                .findFirst()
                .orElse(null);
    }
}