package com.thabo.how_sa_are_you.qna.repository;

import com.thabo.how_sa_are_you.qna.entity.AttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AttemptAnswerRepository extends JpaRepository<AttemptAnswer, UUID> {
}