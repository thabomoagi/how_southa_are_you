package com.thabo.howsouthaareyou.thirtyseconds.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "thirty_seconds_rounds")
public class ThirtySecondsRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private ThirtySecondsGame game;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    @Column(name = "player_name", length = 100)
    private String playerName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String prompt;

    @Column
    private Integer score;
}