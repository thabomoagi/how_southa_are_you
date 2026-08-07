package com.thabo.howsouthaareyou.thirtyseconds.service;

import com.thabo.howsouthaareyou.common.exception.BadRequestException;
import com.thabo.howsouthaareyou.common.exception.NotFoundException;
import com.thabo.howsouthaareyou.thirtyseconds.dto.GameResultResponse;
import com.thabo.howsouthaareyou.thirtyseconds.dto.GameSummaryResponse;
import com.thabo.howsouthaareyou.thirtyseconds.dto.PlayerScoreDto;
import com.thabo.howsouthaareyou.thirtyseconds.dto.RoundDto;
import com.thabo.howsouthaareyou.thirtyseconds.dto.StartGameRequest;
import com.thabo.howsouthaareyou.thirtyseconds.dto.StartGameResponse;
import com.thabo.howsouthaareyou.thirtyseconds.dto.SubmitRoundScoreRequest;
import com.thabo.howsouthaareyou.thirtyseconds.dto.SubmitRoundScoreResponse;
import com.thabo.howsouthaareyou.thirtyseconds.entity.ThirtySecondsCard;
import com.thabo.howsouthaareyou.thirtyseconds.entity.ThirtySecondsGame;
import com.thabo.howsouthaareyou.thirtyseconds.entity.ThirtySecondsMode;
import com.thabo.howsouthaareyou.thirtyseconds.entity.ThirtySecondsRound;
import com.thabo.howsouthaareyou.thirtyseconds.repository.ThirtySecondsCardRepository;
import com.thabo.howsouthaareyou.thirtyseconds.repository.ThirtySecondsGameRepository;
import com.thabo.howsouthaareyou.thirtyseconds.repository.ThirtySecondsRoundRepository;
import com.thabo.howsouthaareyou.user.entity.User;
import com.thabo.howsouthaareyou.user.service.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ThirtySecondsService {

    private static final int ROUND_DURATION_SECONDS = 30;

    private final ThirtySecondsGameRepository gameRepository;
    private final ThirtySecondsRoundRepository roundRepository;
    private final ThirtySecondsCardRepository cardRepository;
    private final CurrentUserProvider currentUserProvider;

    public StartGameResponse startGame(StartGameRequest request) {
        User user = currentUserProvider.getCurrentUser();

        List<String> playerNames = normalizePlayerNames(request);

        int roundsPerPlayer = request.roundsPerPlayer() == null ? 3 : request.roundsPerPlayer();
        int totalRounds = playerNames.size() * roundsPerPlayer;

        List<ThirtySecondsCard> cards = cardRepository.findRandomCards(PageRequest.of(0, totalRounds));

        if (cards.size() < totalRounds) {
            throw new BadRequestException("Not enough cards available in the database. Please seed more content.");
        }

        LocalDateTime startedAt = LocalDateTime.now();

        ThirtySecondsGame game = ThirtySecondsGame.builder()
                .user(user)
                .mode(request.mode())
                .playerCount(playerNames.size())
                .totalScore(0)
                .startedAt(startedAt)
                .build();

        gameRepository.save(game);

        List<ThirtySecondsRound> rounds = new ArrayList<>();

        for (int i = 0; i < totalRounds; i++) {
            String playerName = playerNames.get(i % playerNames.size());
            ThirtySecondsCard card = cards.get(i);

            List<String> shuffledWords = new ArrayList<>(card.getWords());
            Collections.shuffle(shuffledWords);
            String prompt = String.join(", ", shuffledWords);

            rounds.add(
                    ThirtySecondsRound.builder()
                            .game(game)
                            .roundNumber(i + 1)
                            .playerName(playerName)
                            .prompt(prompt)
                            .build());
        }

        roundRepository.saveAll(rounds);

        List<RoundDto> roundDtos = rounds.stream()
                .map(this::toRoundDto)
                .toList();

        return new StartGameResponse(
                game.getId(),
                game.getMode(),
                game.getPlayerCount(),
                roundsPerPlayer,
                totalRounds,
                ROUND_DURATION_SECONDS,
                startedAt,
                roundDtos);
    }

    public SubmitRoundScoreResponse submitRoundScore(UUID gameId, SubmitRoundScoreRequest request) {
        ThirtySecondsGame game = getOwnedGame(gameId);

        if (game.getCompletedAt() != null) {
            throw new BadRequestException("Game is already completed");
        }

        ThirtySecondsRound round = roundRepository.findById(request.roundId())
                .orElseThrow(() -> new NotFoundException("Round not found"));

        if (!round.getGame().getId().equals(game.getId())) {
            throw new BadRequestException("Round does not belong to this game");
        }

        round.setScore(request.score());
        roundRepository.save(round);

        List<ThirtySecondsRound> rounds = roundRepository.findByGameIdOrderByRoundNumberAsc(gameId);

        boolean allRoundsCompleted = rounds.stream()
                .allMatch(r -> r.getScore() != null);

        if (allRoundsCompleted) {
            finishGame(game, rounds);
        }

        return new SubmitRoundScoreResponse(
                round.getId(),
                round.getRoundNumber(),
                round.getPlayerName(),
                round.getScore(),
                allRoundsCompleted);
    }

    public GameResultResponse completeGame(UUID gameId) {
        ThirtySecondsGame game = getOwnedGame(gameId);

        if (game.getCompletedAt() != null) {
            return getGameResult(gameId);
        }

        List<ThirtySecondsRound> rounds = roundRepository.findByGameIdOrderByRoundNumberAsc(gameId);

        for (ThirtySecondsRound round : rounds) {
            if (round.getScore() == null) {
                round.setScore(0);
            }
        }

        roundRepository.saveAll(rounds);

        finishGame(game, rounds);

        List<ThirtySecondsRound> updatedRounds = roundRepository.findByGameIdOrderByRoundNumberAsc(gameId);

        return buildGameResult(game, updatedRounds);
    }

    public GameResultResponse getGameResult(UUID gameId) {
        ThirtySecondsGame game = getOwnedGame(gameId);

        if (game.getCompletedAt() == null) {
            throw new BadRequestException("Game is not completed yet");
        }

        List<ThirtySecondsRound> rounds = roundRepository.findByGameIdOrderByRoundNumberAsc(gameId);

        return buildGameResult(game, rounds);
    }

    public Page<GameSummaryResponse> getGameHistory(Pageable pageable) {
        User user = currentUserProvider.getCurrentUser();

        return gameRepository.findByUserIdOrderByStartedAtDesc(user.getId(), pageable)
                .map(this::toGameSummaryResponse);
    }

    private ThirtySecondsGame getOwnedGame(UUID gameId) {
        User user = currentUserProvider.getCurrentUser();

        return gameRepository.findByIdAndUserId(gameId, user.getId())
                .orElseThrow(() -> new NotFoundException("Game not found"));
    }

    private List<String> normalizePlayerNames(StartGameRequest request) {
        List<String> names = request.playerNames() == null
                ? List.of()
                : request.playerNames()
                        .stream()
                        .map(String::trim)
                        .filter(name -> !name.isBlank())
                        .toList();

        if (request.mode() == ThirtySecondsMode.SOLO) {
            return names.isEmpty() ? List.of("You") : List.of(names.get(0));
        }

        if (names.isEmpty()) {
            throw new BadRequestException("Player names are required for LOCAL mode");
        }

        if (names.size() > 8) {
            throw new BadRequestException("Maximum of 8 players allowed");
        }

        return names;
    }

    private void finishGame(ThirtySecondsGame game, List<ThirtySecondsRound> rounds) {
        List<PlayerScoreDto> playerScores = getPlayerScores(rounds);

        int totalScore = calculateTotalScore(rounds);
        String winner = calculateWinner(playerScores);

        game.setCompletedAt(LocalDateTime.now());
        game.setTotalScore(totalScore);
        game.setWinningPlayerName(winner);

        gameRepository.save(game);
    }

    private GameResultResponse buildGameResult(ThirtySecondsGame game, List<ThirtySecondsRound> rounds) {
        List<PlayerScoreDto> playerScores = getPlayerScores(rounds);

        Integer totalScore = game.getTotalScore() != null
                ? game.getTotalScore()
                : calculateTotalScore(rounds);

        String winner = game.getWinningPlayerName() != null
                ? game.getWinningPlayerName()
                : calculateWinner(playerScores);

        List<RoundDto> roundDtos = rounds.stream()
                .map(this::toRoundDto)
                .toList();

        return new GameResultResponse(
                game.getId(),
                game.getMode(),
                game.getPlayerCount(),
                totalScore,
                winner,
                game.getStartedAt(),
                game.getCompletedAt(),
                playerScores,
                roundDtos);
    }

    private List<PlayerScoreDto> getPlayerScores(List<ThirtySecondsRound> rounds) {
        Map<String, Integer> scores = new LinkedHashMap<>();

        for (ThirtySecondsRound round : rounds) {
            String playerName = round.getPlayerName() == null ? "Unknown" : round.getPlayerName();
            int score = round.getScore() == null ? 0 : round.getScore();

            scores.put(playerName, scores.getOrDefault(playerName, 0) + score);
        }

        return scores.entrySet()
                .stream()
                .map(entry -> new PlayerScoreDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    private int calculateTotalScore(List<ThirtySecondsRound> rounds) {
        return rounds.stream()
                .filter(round -> round.getScore() != null)
                .mapToInt(ThirtySecondsRound::getScore)
                .sum();
    }

    private String calculateWinner(List<PlayerScoreDto> playerScores) {
        return playerScores.stream()
                .max(Comparator.comparing(PlayerScoreDto::score))
                .map(PlayerScoreDto::playerName)
                .orElse(null);
    }

    private RoundDto toRoundDto(ThirtySecondsRound round) {
        return new RoundDto(
                round.getId(),
                round.getRoundNumber(),
                round.getPlayerName(),
                round.getPrompt(),
                round.getScore());
    }

    private GameSummaryResponse toGameSummaryResponse(ThirtySecondsGame game) {
        return new GameSummaryResponse(
                game.getId(),
                game.getMode(),
                game.getPlayerCount(),
                game.getTotalScore(),
                game.getWinningPlayerName(),
                game.getStartedAt(),
                game.getCompletedAt());
    }
}