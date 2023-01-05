package com.example.dailychallenge.controller;

import com.example.dailychallenge.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller @RequiredArgsConstructor
public class UserController {

    private final UserService userService;



}
