package com.thabo.howsouthaareyou.qna.repository;

import com.thabo.howsouthaareyou.qna.entity.AttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttemptAnswerRepository extends JpaRepository<AttemptAnswer, Long> {

    List<AttemptAnswer> findByAttemptId(UUID attemptId);

    Optional<AttemptAnswer> findByAttemptIdAndQuestionId(UUID attemptId, Long questionId);

    boolean existsByAttemptIdAndQuestionId(UUID attemptId, Long questionId);
}