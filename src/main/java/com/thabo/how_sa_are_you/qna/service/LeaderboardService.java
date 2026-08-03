package com.thabo.how_sa_are_you.qna.service;

import com.thabo.how_sa_are_you.qna.dto.LeaderboardEntryDto;
import com.thabo.how_sa_are_you.qna.dto.LeaderboardResponse;
import com.thabo.how_sa_are_you.qna.repository.AttemptRepository;
import com.thabo.how_sa_are_you.qna.repository.LeaderboardRow;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderboardService {

    private final AttemptRepository attemptRepository;

    public LeaderboardService(AttemptRepository attemptRepository) {
        this.attemptRepository = attemptRepository;
    }

    public LeaderboardResponse getLeaderboard(int page, int size) {
        List<LeaderboardRow> rows = attemptRepository.findLeaderboard(PageRequest.of(page, size));

        int startRank = page * size + 1;
        List<LeaderboardEntryDto> entries = new java.util.ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            LeaderboardRow row = rows.get(i);
            entries.add(new LeaderboardEntryDto(startRank + i, row.getUsername(), row.getBestScore()));
        }

        return new LeaderboardResponse(entries, page, size);
    }
}