package com.thabo.howsouthaareyou.qna.repository;

import com.thabo.howsouthaareyou.qna.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByActiveTrue();

    Page<Question> findByActiveTrue(Pageable pageable);

    Page<Question> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);

    @Query(value = "SELECT * FROM questions WHERE is_active = true ORDER BY random()", nativeQuery = true)
    List<Question> findRandomActiveQuestions(Pageable pageable);

    @Query(value = "SELECT * FROM questions WHERE is_active = true AND category_id = :categoryId ORDER BY random()", nativeQuery = true)
    List<Question> findRandomActiveQuestionsByCategoryId(
            @Param("categoryId") Long categoryId,
            Pageable pageable);

    long countByCategoryId(Long categoryId);

    boolean existsByExternalId(String externalId);
}