package com.example.dailychallenge.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CorsController {

    @GetMapping("/testCors")
    public String cors() {
        return "ok";
    }
}
