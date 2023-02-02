package com.example.dailychallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DailyChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(DailyChallengeApplication.class, args);
	}

}
