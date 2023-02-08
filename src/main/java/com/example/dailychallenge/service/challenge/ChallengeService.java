package com.example.dailychallenge.service.challenge;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.dto.ChallengeEditor;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeImg;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.challenge.ChallengeNotFound;
import com.example.dailychallenge.repository.ChallengeRepository;
import com.example.dailychallenge.vo.RequestUpdateChallenge;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeImgService challengeImgService;

    public Challenge saveChallenge(ChallengeDto challengeDto, List<MultipartFile> challengeImgFiles, User user) {
        Challenge challenge = challengeDto.toChallenge();
        challenge.setUser(user);

        challengeRepository.save(challenge);

        if (challengeImgFiles != null) {
            for (MultipartFile challengeImgFile : challengeImgFiles) {
                ChallengeImg challengeImg = new ChallengeImg();
                challengeImg.setChallenge(challenge);
                challengeImgService.saveChallengeImg(challengeImg, challengeImgFile);
            }
        }

        return challenge;
    }

    // TODO: 2023-02-06 댓글, 해시태그 정보도 같이 반환하기
    public Challenge findById(Long id) {
        return challengeRepository.findById(id).orElseThrow(ChallengeNotFound::new);
    }

    /**
     * 기존 이미지들을 전부 삭제하고 업데이트 이미지들을 저장하는 로직
     * 게시글에 다중 이미지가 필요한가
     */
    public Challenge updateChallenge(Long challengeId, RequestUpdateChallenge requestUpdateChallenge,
                                     List<MultipartFile> updateChallengeImgFiles) {
        Challenge findChallenge = challengeRepository.findById(challengeId).orElseThrow(ChallengeNotFound::new);

        ChallengeEditor.ChallengeEditorBuilder editorBuilder = findChallenge.toEditor();
        ChallengeEditor challengeEditor = editorBuilder
                .title(requestUpdateChallenge.getTitle())
                .content(requestUpdateChallenge.getContent())
                .category(requestUpdateChallenge.getChallengeCategory())
                .build();

        findChallenge.update(challengeEditor);

//        List<ChallengeImg> challengeImgs = findChallenge.getChallengeImgs();
//        List<Long> deleteCommentImgIds = new ArrayList<>();
//        int idx = 0;
//        if (!CollectionUtils.isEmpty(updateChallengeImgFiles)) {
//
//        }

        return findChallenge;
    }

    public void deleteChallenge(Long challengeId, User user) {
        Challenge findChallenge = challengeRepository.findById(challengeId).orElseThrow(ChallengeNotFound::new);

        findChallenge.validateOwner(user.getId());

        challengeRepository.delete(findChallenge);
    }
}
