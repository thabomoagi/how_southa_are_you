package com.thabo.howsouthaareyou.qna.service;

import com.thabo.howsouthaareyou.qna.dto.LeaderboardEntryDto;
import com.thabo.howsouthaareyou.qna.dto.LeaderboardResponse;
import com.thabo.howsouthaareyou.qna.repository.LeaderboardProjection;
import com.thabo.howsouthaareyou.qna.repository.LeaderboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;

    public LeaderboardResponse getLeaderboard(String period, int limit) {
        String normalizedPeriod = period == null ? "ALL" : period.toUpperCase();

        int safeLimit = Math.min(Math.max(limit, 1), 50);

        Pageable pageable = PageRequest.of(0, safeLimit);

        List<LeaderboardProjection> projections = switch (normalizedPeriod) {
            case "WEEKLY" -> leaderboardRepository.findTopSince(
                    LocalDateTime.now().minusDays(7),
                    pageable);
            case "MONTHLY" -> leaderboardRepository.findTopSince(
                    LocalDateTime.now().minusDays(30),
                    pageable);
            default -> leaderboardRepository.findTopAll(pageable);
        };

        List<LeaderboardEntryDto> entries = new ArrayList<>();

        for (int i = 0; i < projections.size(); i++) {
            LeaderboardProjection projection = projections.get(i);

            entries.add(
                    new LeaderboardEntryDto(
                            i + 1,
                            projection.getUserId(),
                            projection.getUsername(),
                            projection.getProfilePictureUrl(),
                            projection.getScore()));
        }

        return new LeaderboardResponse(normalizedPeriod, entries);
    }
}