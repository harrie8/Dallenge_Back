package com.example.dailychallenge.repository;

import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.repository.challenge.UserChallengeRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long>, UserChallengeRepositoryCustom {
}
