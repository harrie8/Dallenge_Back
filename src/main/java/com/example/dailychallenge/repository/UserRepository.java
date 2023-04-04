package com.example.dailychallenge.repository;

import com.example.dailychallenge.entity.users.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndRegistrationId(String email, String registrationId);
}
