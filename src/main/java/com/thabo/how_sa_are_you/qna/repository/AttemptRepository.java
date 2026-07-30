package com.thabo.how_sa_are_you.qna.repository;

import com.thabo.how_sa_are_you.qna.entity.Attempt;
import com.thabo.how_sa_are_you.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AttemptRepository extends JpaRepository<Attempt, UUID> {

    List<Attempt> findByUser(User user);

}