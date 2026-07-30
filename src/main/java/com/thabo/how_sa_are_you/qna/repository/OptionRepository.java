package com.thabo.how_sa_are_you.qna.repository;

import com.thabo.how_sa_are_you.qna.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OptionRepository extends JpaRepository<Option, UUID> {
}