package com.thabo.howsouthaareyou.qna.repository;

import com.thabo.howsouthaareyou.qna.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {

    List<Option> findByQuestionId(Long questionId);

    List<Option> findByQuestionIdIn(List<Long> questionIds);

    Optional<Option> findByIdAndQuestionId(Long id, Long questionId);

    long countByQuestionId(Long questionId);
}