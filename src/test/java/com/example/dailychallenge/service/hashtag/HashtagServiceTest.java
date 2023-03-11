package com.example.dailychallenge.service.hashtag;

import static com.example.dailychallenge.util.fixture.challenge.ChallengeFixture.createChallengeDto;
import static com.example.dailychallenge.util.fixture.user.UserFixture.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.util.ServiceTest;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class HashtagServiceTest extends ServiceTest {

    @Autowired
    private HashtagService hashtagService;
    @Autowired
    private UserService userService;
    @Autowired
    private ChallengeService challengeService;
    @Autowired
    EntityManager entityManager;

    private User savedUser;
    private Challenge challenge;

    @BeforeEach
    void beforeEach() {
        savedUser = userService.saveUser(createUser(), passwordEncoder);
        challenge = challengeService.saveChallenge(createChallengeDto(), null, savedUser);
    }

    @Test
    @DisplayName("해시태그 생성 테스트")
    public void saveHashtagTest() {
        List<String> hashtagDto = List.of("tag1");
        List<Hashtag> hashtags = hashtagService.saveHashtag(hashtagDto);

        assertEquals(hashtagDto.get(0), hashtags.get(0).getContent());
        assertEquals(1, hashtags.get(0).getTagCount());
    }

    @Test
    @DisplayName("해시태그 수정 테스트")
    public void updateHashtagTest() {
        List<String> updateHashtagDto = List.of("editTag1", "editTag2");
        Long challengeId = challenge.getId();

        List<Hashtag> updateHashtags = hashtagService.updateHashtag(updateHashtagDto, challengeId);

        List<String> updateHashtagsContent = updateHashtags.stream()
                .map(Hashtag::getContent)
                .collect(Collectors.toUnmodifiableList());
        assertEquals(updateHashtagDto, updateHashtagsContent);

        boolean isUpdateHashtagsCountOne = updateHashtags.stream()
                .map(Hashtag::getTagCount)
                .allMatch(count -> count == 1);
        assertTrue(isUpdateHashtagsCountOne);
    }

    @Test
    @DisplayName("가장 많이 작성된 3개의 해시태그들을 찾는 테스트")
    void searchAllTest() {
        List<String> hashtagDto = List.of("tag1", "tag1", "tag2", "tag2", "tag2", "tag3", "tag4");
        hashtagService.saveHashtag(hashtagDto);

        List<Hashtag> results = hashtagService.searchThreeMostWrittenHashtags();

        assertEquals("tag2", results.get(0).getContent());
        assertEquals("tag1", results.get(1).getContent());
        assertEquals("tag3", results.get(2).getContent());
    }
}