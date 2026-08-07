package com.thabo.howsouthaareyou.qna.service;

import com.thabo.howsouthaareyou.qna.dto.AttemptSummaryResponse;
import com.thabo.howsouthaareyou.qna.entity.Attempt;
import com.thabo.howsouthaareyou.qna.repository.AttemptRepository;
import com.thabo.howsouthaareyou.user.entity.User;
import com.thabo.howsouthaareyou.user.service.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttemptHistoryService {

    private final AttemptRepository attemptRepository;
    private final CurrentUserProvider currentUserProvider;

    public Page<AttemptSummaryResponse> getAttemptHistory(Pageable pageable) {
        User user = currentUserProvider.getCurrentUser();

        return attemptRepository.findByUserIdOrderByStartedAtDesc(user.getId(), pageable)
                .map(this::toAttemptSummaryResponse);
    }

    private AttemptSummaryResponse toAttemptSummaryResponse(Attempt attempt) {
        return new AttemptSummaryResponse(
                attempt.getId(),
                attempt.getScore(),
                attempt.getCorrectCount(),
                attempt.getTotalQuestions(),
                attempt.getStartedAt(),
                attempt.getCompletedAt());
    }
}