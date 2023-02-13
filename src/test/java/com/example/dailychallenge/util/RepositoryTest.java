package com.example.dailychallenge.util;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
//@Import(JPAConfig.class)
//@Configuration
//@EnableJpaAuditing
@TestPropertySource(locations = "classpath:application-test.properties")
public class RepositoryTest {
}
