package com.example.dailychallenge.repository;

import com.example.dailychallenge.entity.users.UserImg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserImgRepository extends JpaRepository<UserImg,Long> {
}
