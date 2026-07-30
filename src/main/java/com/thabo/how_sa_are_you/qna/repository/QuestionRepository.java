package com.thabo.how_sa_are_you.qna.repository;

import com.thabo.how_sa_are_you.qna.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {

    List<Question> findByActiveTrue();

}