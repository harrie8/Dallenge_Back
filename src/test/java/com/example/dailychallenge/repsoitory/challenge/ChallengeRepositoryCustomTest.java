package com.example.dailychallenge.repsoitory.challenge;

import static com.example.dailychallenge.entity.challenge.ChallengeCategory.ECONOMY;
import static com.example.dailychallenge.entity.challenge.ChallengeCategory.STUDY;
import static com.example.dailychallenge.entity.challenge.ChallengeCategory.WORKOUT;
import static com.example.dailychallenge.entity.challenge.ChallengeDuration.OVER_ONE_HOUR;
import static com.example.dailychallenge.entity.challenge.ChallengeDuration.WITHIN_TEN_MINUTES;
import static com.example.dailychallenge.entity.challenge.ChallengeLocation.INDOOR;
import static com.example.dailychallenge.entity.challenge.ChallengeLocation.OUTDOOR;
import static com.example.dailychallenge.entity.challenge.ChallengeStatus.PAUSE;
import static com.example.dailychallenge.entity.challenge.ChallengeStatus.TRYING;
import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;
import static com.example.dailychallenge.util.fixture.challenge.ChallengeFixture.createSpecificChallenge;
import static com.example.dailychallenge.util.fixture.challenge.ChallengeImgFixture.createSpecificChallengeImgs;
import static com.example.dailychallenge.util.fixture.challengeHashtag.ChallengeHashtagFixture.createSpecificChallengeHashtags;
import static com.example.dailychallenge.util.fixture.hashtag.HashtagFixture.createSpecificHashtags;
import static com.example.dailychallenge.util.fixture.user.UserFixture.USERNAME;
import static com.example.dailychallenge.util.fixture.userChallenge.UserChallengeFixture.createSpecificUserChallenge;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeImg;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.hashtag.ChallengeHashtag;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.challenge.ChallengeNotFound;
import com.example.dailychallenge.repository.ChallengeHashtagRepository;
import com.example.dailychallenge.repository.ChallengeImgRepository;
import com.example.dailychallenge.repository.ChallengeRepository;
import com.example.dailychallenge.repository.HashtagRepository;
import com.example.dailychallenge.repository.UserChallengeRepository;
import com.example.dailychallenge.util.RepositoryTest;
import com.example.dailychallenge.util.fixture.TestDataSetup;
import com.example.dailychallenge.vo.challenge.ResponseChallenge;
import com.example.dailychallenge.vo.challenge.ResponseRecommendedChallenge;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ChallengeRepositoryCustomTest extends RepositoryTest {

    @Autowired
    private UserChallengeRepository userChallengeRepository;
    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private ChallengeImgRepository challengeImgRepository;
    @Autowired
    private ChallengeHashtagRepository challengeHashtagRepository;
    @Autowired
    private HashtagRepository hashtagRepository;
    @Autowired
    private TestDataSetup testDataSetup;

    private User user;
    private Challenge challenge1;

    @BeforeEach
    void beforeEach() {
        initData();
    }

    private void initData() {
        user = testDataSetup.saveUser(USERNAME, EMAIL, PASSWORD);

        challenge1 = saveChallenge("제목입니다.1", "내용입니다.1", STUDY, INDOOR, WITHIN_TEN_MINUTES, user);
        saveChallengeImgs("imgUrl", "imgName", "oriImgName", challenge1, 2);
        saveChallengeParticipate(TRYING, user, challenge1);
        saveChallengeHashtags(List.of("tag1", "tag2", "tag3"), challenge1);

        Challenge challenge2 = saveChallenge("제목입니다.2", "내용입니다.2", ECONOMY, OUTDOOR, OVER_ONE_HOUR, user);
        saveChallengeParticipate(PAUSE, user, challenge2);

        Challenge challenge6 = null;

        for (int i = 3; i <= 10; i++) {
            Challenge challenge = saveChallenge("제목입니다." + i, "내용입니다." + i, WORKOUT, INDOOR,
                    WITHIN_TEN_MINUTES, user);
            saveChallengeParticipate(TRYING, user, challenge);

            if (i == 6) {
                challenge6 = challenge;
            }
        }

        for (int i = 1; i <= 8; i++) {
            User user = testDataSetup.saveUser(USERNAME + i, i + "@test.com", PASSWORD);
            if (i == 1) {
                saveChallengeParticipate(TRYING, user, challenge1);
            }
            if (2 <= i && i <= 5) {
                saveChallengeParticipate(PAUSE, user, challenge2);
            }
            if (i == 6) {
                saveChallengeParticipate(TRYING, user, challenge6);
            }
        }
    }

    private Challenge saveChallenge(String title, String content, ChallengeCategory challengeCategory,
                                    ChallengeLocation challengeLocation, ChallengeDuration challengeDuration, User user) {

        Challenge challenge = createSpecificChallenge(title, content, challengeCategory, challengeLocation,
                challengeDuration, user);
        challengeRepository.save(challenge);

        return challenge;
    }

    private void saveChallengeImgs(String imgUrl, String imgName, String oriImgName, Challenge challenge,
                                   int repeatCount) {
        List<ChallengeImg> specificChallengeImgs = createSpecificChallengeImgs(imgUrl, imgName, oriImgName, challenge,
                repeatCount);
        challengeImgRepository.saveAll(specificChallengeImgs);
    }

    private void saveChallengeParticipate(ChallengeStatus challengeStatus, User user, Challenge challenge) {
        UserChallenge userChallenge = createSpecificUserChallenge(challengeStatus, user, challenge);
        userChallengeRepository.save(userChallenge);
    }

    private void saveChallengeHashtags(List<String> hashtagDto, Challenge challenge) {
        List<Hashtag> hashtags = createSpecificHashtags(hashtagDto);
        hashtagRepository.saveAll(hashtags);

        List<ChallengeHashtag> challengeHashtags = createSpecificChallengeHashtags(hashtags, challenge);
        challengeHashtagRepository.saveAll(challengeHashtags);
    }

    @Nested
    @DisplayName("특정 챌린지 조회 테스트")
    class searchById {
        @Test
        void success() {
            Long challenge1Id = challenge1.getId();
            ResponseChallenge responseChallenge = challengeRepository.searchChallengeById(challenge1Id)
                    .orElseThrow(ChallengeNotFound::new);

            assertAll(() -> {
                assertEquals(challenge1Id, responseChallenge.getId());
                assertEquals(challenge1.getTitle(), responseChallenge.getTitle());
                assertEquals(challenge1.getContent(), responseChallenge.getContent());
                assertEquals(challenge1.getChallengeCategory().getDescription(), responseChallenge.getChallengeCategory());
                assertEquals(challenge1.getChallengeLocation().getDescription(), responseChallenge.getChallengeLocation());
                assertEquals(challenge1.getChallengeDuration().getDescription(), responseChallenge.getChallengeDuration());
                assertEquals(challenge1.getFormattedCreatedAt(), responseChallenge.getCreated_at());
                assertEquals(challenge1.getImgUrls(), responseChallenge.getChallengeImgUrls());
                assertEquals(challenge1.getHashtags(), responseChallenge.getChallengeHashtags());
                assertEquals(2, responseChallenge.getHowManyUsersAreInThisChallenge());
                assertEquals(user.getUserName(), responseChallenge.getChallengeOwnerUser().getUserName());
                assertEquals(user.getEmail(), responseChallenge.getChallengeOwnerUser().getEmail());
                assertEquals(user.getId(), responseChallenge.getChallengeOwnerUser().getUserId());
            });
        }

        @Test
        void failByChallengeNotFound() {
            Long notExistChallenge1Id = challenge1.getId() + 100L;

            Throwable exception = assertThrows(ChallengeNotFound.class,
                    () -> challengeRepository.searchChallengeById(notExistChallenge1Id)
                            .orElseThrow(ChallengeNotFound::new));
            assertEquals("챌린지를 찾을 수 없습니다.", exception.getMessage());
        }
    }

    @Test
    @DisplayName("질문으로 챌린지들 조회 테스트")
    void searchChallengesByQuestionTest() {
        ChallengeCategory challengeCategory = WORKOUT;
        ChallengeDuration challengeDuration = WITHIN_TEN_MINUTES;
        ChallengeLocation challengeLocation = INDOOR;

        List<ResponseRecommendedChallenge> results = challengeRepository.searchChallengesByQuestion(
                challengeCategory, challengeDuration, challengeLocation);

        assertAll(() -> {
            assertThat(results).extracting("title").containsExactlyElementsOf(
                    List.of("제목입니다.3", "제목입니다.4", "제목입니다.5", "제목입니다.6"));
            assertThat(results).extracting("content").containsExactlyElementsOf(
                    List.of("내용입니다.3", "내용입니다.4", "내용입니다.5", "내용입니다.6"));
            assertThat(results).extracting("challengeImgUrls").containsExactly(
                    List.of(), List.of(), List.of(), List.of()
            );
        });
    }

    @Test
    @DisplayName("랜덤으로 챌린지 조회 테스트")
    void searchChallengeByRandomTest() {
        ResponseRecommendedChallenge result = challengeRepository.searchChallengeByRandom();

        assertAll(() -> {
            assertNotNull(result.getId());
            assertThat(result.getTitle()).startsWith("제목입니다.");
            assertThat(result.getContent()).startsWith("내용입니다.");
            if (result.getChallengeImgUrls().isEmpty()) {
                assertEquals(Collections.emptyList(), result.getChallengeImgUrls());
            }
            if (!result.getChallengeImgUrls().isEmpty()) {
                assertEquals(List.of("imgUrl","imgUrl"), result.getChallengeImgUrls());
            }
        });
    }
}
