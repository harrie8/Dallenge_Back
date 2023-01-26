package com.example.dailychallenge.repository;

import com.example.dailychallenge.entity.challenge.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {
}
