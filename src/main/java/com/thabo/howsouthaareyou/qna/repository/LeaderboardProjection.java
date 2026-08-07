package com.thabo.howsouthaareyou.qna.repository;

import java.util.UUID;

public interface LeaderboardProjection {

    UUID getUserId();

    String getUsername();

    String getProfilePictureUrl();

    Integer getScore();
}