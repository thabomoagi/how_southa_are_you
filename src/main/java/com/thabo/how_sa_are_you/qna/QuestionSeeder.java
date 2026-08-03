package com.thabo.how_sa_are_you.qna;

import com.thabo.how_sa_are_you.qna.entity.Option;
import com.thabo.how_sa_are_you.qna.entity.Question;
import com.thabo.how_sa_are_you.qna.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class QuestionSeeder implements CommandLineRunner {

    private final QuestionRepository questionRepository;

    @Value("${app.seed-questions:false}")
    private boolean seedEnabled;

    public QuestionSeeder(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!seedEnabled)
            return;
        if (questionRepository.count() > 0) {
            System.out.println("Questions already seeded, skipping.");
            return;
        }

        List<Question> questions = new ArrayList<>();
        ClassPathResource resource = new ClassPathResource("data/qna_questions.csv");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String header = reader.readLine(); // skip header row
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank())
                    continue;
                String[] fields = line.split(",", -1);
                if (fields.length < 7)
                    continue;

                String category = fields[0].trim();
                String text = fields[1].trim();
                String[] optionTexts = { fields[2].trim(), fields[3].trim(), fields[4].trim(), fields[5].trim() };
                String correctLetter = fields[6].trim().toUpperCase();

                Question question = new Question();
                question.setCategory(category);
                question.setText(text);
                question.setActive(true);

                String[] letters = { "A", "B", "C", "D" };
                for (int i = 0; i < 4; i++) {
                    Option option = new Option();
                    option.setText(optionTexts[i]);
                    option.setCorrect(letters[i].equals(correctLetter));
                    option.setQuestion(question);
                    question.getOptions().add(option);
                }

                questions.add(question);
            }
        }

        questionRepository.saveAll(questions);
        System.out.println("Seeded " + questions.size() + " questions.");
    }
}