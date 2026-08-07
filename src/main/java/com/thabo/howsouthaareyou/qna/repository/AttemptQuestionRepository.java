package com.thabo.howsouthaareyou.qna.repository;

import com.thabo.howsouthaareyou.qna.entity.AttemptQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttemptQuestionRepository extends JpaRepository<AttemptQuestion, Long> {

    List<AttemptQuestion> findByAttemptIdOrderByPositionAsc(UUID attemptId);

    boolean existsByAttemptIdAndQuestionId(UUID attemptId, Long questionId);
}