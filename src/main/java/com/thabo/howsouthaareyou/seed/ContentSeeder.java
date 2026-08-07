package com.thabo.howsouthaareyou.seed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thabo.howsouthaareyou.qna.entity.Category;
import com.thabo.howsouthaareyou.qna.entity.Difficulty;
import com.thabo.howsouthaareyou.qna.entity.Option;
import com.thabo.howsouthaareyou.qna.entity.Question;
import com.thabo.howsouthaareyou.qna.repository.CategoryRepository;
import com.thabo.howsouthaareyou.qna.repository.QuestionRepository;
import com.thabo.howsouthaareyou.seed.dto.QnaSeedDto;
import com.thabo.howsouthaareyou.seed.dto.ThirtySecondsSeedDto;
import com.thabo.howsouthaareyou.thirtyseconds.entity.ThirtySecondsCard;
import com.thabo.howsouthaareyou.thirtyseconds.repository.ThirtySecondsCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed-questions", havingValue = "true")
public class ContentSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final ThirtySecondsCardRepository thirtySecondsCardRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting content seeding...");
        seedQnaQuestions();
        seedThirtySecondsCards();
        log.info("Content seeding completed.");
    }

    private void seedQnaQuestions() throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:content/qna/*.jsonl");

        int count = 0;

        for (Resource resource : resources) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }

                    try {
                        QnaSeedDto dto = objectMapper.readValue(line, QnaSeedDto.class);

                        if (!questionRepository.existsByExternalId(dto.externalId())) {
                            saveQuestion(dto);
                            count++;
                        }
                    } catch (Exception exception) {
                        log.warn("Failed to parse QnA line: {}", exception.getMessage());
                    }
                }
            }
        }

        log.info("Seeded {} new QnA questions.", count);
    }

    private void seedThirtySecondsCards() throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:content/thirty-seconds/*.jsonl");

        int count = 0;

        for (Resource resource : resources) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }

                    try {
                        ThirtySecondsSeedDto dto = objectMapper.readValue(line, ThirtySecondsSeedDto.class);

                        if (!thirtySecondsCardRepository.existsByExternalId(dto.externalId())) {
                            thirtySecondsCardRepository.save(
                                    ThirtySecondsCard.builder()
                                            .externalId(dto.externalId())
                                            .difficulty(dto.difficulty())
                                            .words(dto.words())
                                            .build());
                            count++;
                        }
                    } catch (Exception exception) {
                        log.warn("Failed to parse Thirty Seconds line: {}", exception.getMessage());
                    }
                }
            }
        }

        log.info("Seeded {} new Thirty Seconds cards.", count);
    }

    private void saveQuestion(QnaSeedDto dto) {
        Category category = getOrCreateCategory(dto.category());

        Question question = Question.builder()
                .externalId(dto.externalId())
                .category(category)
                .prompt(dto.question())
                .difficulty(Difficulty.valueOf(dto.difficulty()))
                .era(dto.era())
                .explanation(dto.explanation())
                .active(true)
                .build();

        List<Option> options = dto.options()
                .stream()
                .map(optionDto -> Option.builder()
                        .question(question)
                        .optionText(optionDto.text())
                        .correct(optionDto.correct())
                        .build())
                .toList();

        question.getOptions().addAll(options);

        questionRepository.save(question);
    }

    private Category getOrCreateCategory(String categoryName) {
        String slug = toSlug(categoryName);

        return categoryRepository.findBySlug(slug)
                .orElseGet(() -> categoryRepository.save(
                        Category.builder()
                                .name(categoryName)
                                .slug(slug)
                                .build()));
    }

    private String toSlug(String input) {
        return input.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }
}