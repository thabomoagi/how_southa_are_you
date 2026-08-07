package com.thabo.howsouthaareyou.qna.service;

import com.thabo.howsouthaareyou.common.exception.BadRequestException;
import com.thabo.howsouthaareyou.common.exception.NotFoundException;
import com.thabo.howsouthaareyou.qna.dto.CreateQuestionRequest;
import com.thabo.howsouthaareyou.qna.dto.OptionAdminDto;
import com.thabo.howsouthaareyou.qna.dto.QuestionAdminDto;
import com.thabo.howsouthaareyou.qna.entity.Category;
import com.thabo.howsouthaareyou.qna.entity.Option;
import com.thabo.howsouthaareyou.qna.entity.Question;
import com.thabo.howsouthaareyou.qna.repository.CategoryRepository;
import com.thabo.howsouthaareyou.qna.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionAdminService {

    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;

    public QuestionAdminDto createQuestion(CreateQuestionRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        if (request.options() == null || request.options().size() < 2) {
            throw new BadRequestException("Question must have at least two options");
        }

        long correctOptionCount = request.options()
                .stream()
                .filter(option -> Boolean.TRUE.equals(option.correct()))
                .count();

        if (correctOptionCount != 1) {
            throw new BadRequestException("Question must have exactly one correct option");
        }

        Question question = Question.builder()
                .category(category)
                .prompt(request.prompt())
                .difficulty(request.difficulty())
                .active(true)
                .build();

        List<Option> options = request.options()
                .stream()
                .map(optionRequest -> Option.builder()
                        .question(question)
                        .optionText(optionRequest.optionText())
                        .correct(optionRequest.correct())
                        .build())
                .toList();

        question.getOptions().addAll(options);

        questionRepository.save(question);

        return toQuestionAdminDto(question);
    }

    public QuestionAdminDto getQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Question not found"));

        return toQuestionAdminDto(question);
    }

    public Page<QuestionAdminDto> getQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable)
                .map(this::toQuestionAdminDto);
    }

    public QuestionAdminDto activateQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Question not found"));

        question.setActive(true);
        questionRepository.save(question);

        return toQuestionAdminDto(question);
    }

    public QuestionAdminDto deactivateQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Question not found"));

        question.setActive(false);
        questionRepository.save(question);

        return toQuestionAdminDto(question);
    }

    private QuestionAdminDto toQuestionAdminDto(Question question) {
        List<OptionAdminDto> options = question.getOptions()
                .stream()
                .sorted(Comparator.comparing(Option::getId))
                .map(option -> new OptionAdminDto(
                        option.getId(),
                        option.getOptionText(),
                        option.getCorrect()))
                .toList();

        return new QuestionAdminDto(
                question.getId(),
                question.getCategory() != null ? question.getCategory().getId() : null,
                question.getCategory() != null ? question.getCategory().getName() : null,
                question.getPrompt(),
                question.getDifficulty(),
                question.getActive(),
                options);
    }
}