package com.thabo.how_sa_are_you.qna.service;

import com.thabo.how_sa_are_you.qna.dto.*;
import com.thabo.how_sa_are_you.qna.entity.Option;
import com.thabo.how_sa_are_you.qna.entity.Question;
import com.thabo.how_sa_are_you.qna.repository.QuestionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class QuestionAdminService {

    private final QuestionRepository questionRepository;

    public QuestionAdminService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Transactional
    public QuestionAdminDto create(CreateQuestionRequest request) {
        validateExactlyOneCorrect(request.options());

        Question question = new Question();
        question.setText(request.text());
        question.setCategory(request.category());
        question.setActive(true);

        for (CreateOptionRequest opt : request.options()) {
            Option option = new Option();
            option.setText(opt.text());
            option.setCorrect(opt.correct());
            option.setQuestion(question);
            question.getOptions().add(option);
        }

        questionRepository.save(question);
        return toAdminDto(question);
    }

    @Transactional
    public QuestionAdminDto update(UUID id, CreateQuestionRequest request) {
        validateExactlyOneCorrect(request.options());

        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));

        question.setText(request.text());
        question.setCategory(request.category());

        question.getOptions().clear(); // orphanRemoval = true on this relation deletes the old rows
        for (CreateOptionRequest opt : request.options()) {
            Option option = new Option();
            option.setText(opt.text());
            option.setCorrect(opt.correct());
            option.setQuestion(question);
            question.getOptions().add(option);
        }

        questionRepository.save(question);
        return toAdminDto(question);
    }

    @Transactional
    public void deactivate(UUID id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));
        question.setActive(false);
        questionRepository.save(question);
    }

    @Transactional
    public void reactivate(UUID id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));
        question.setActive(true);
        questionRepository.save(question);
    }

    public List<QuestionAdminDto> listAll() {
        return questionRepository.findAll().stream().map(this::toAdminDto).toList();
    }

    private void validateExactlyOneCorrect(List<CreateOptionRequest> options) {
        long correctCount = options.stream().filter(CreateOptionRequest::correct).count();
        if (correctCount != 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Exactly one option must be marked correct, got " + correctCount);
        }
    }

    private QuestionAdminDto toAdminDto(Question q) {
        List<OptionAdminDto> opts = q.getOptions().stream()
                .map(o -> new OptionAdminDto(o.getId(), o.getText(), o.isCorrect()))
                .toList();
        return new QuestionAdminDto(q.getId(), q.getText(), q.getCategory(), q.isActive(), opts);
    }
}