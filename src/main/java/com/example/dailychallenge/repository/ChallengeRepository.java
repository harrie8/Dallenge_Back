package com.example.dailychallenge.repository;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.repository.challenge.ChallengeRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenge, Long>, ChallengeRepositoryCustom {
}
