package com.example.dailychallenge.service.challenge;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.dto.ChallengeEditor;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeImg;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.AuthorizationException;
import com.example.dailychallenge.exception.challenge.ChallengeNotFound;
import com.example.dailychallenge.repository.ChallengeRepository;
import com.example.dailychallenge.vo.challenge.RequestUpdateChallenge;
import com.example.dailychallenge.vo.challenge.ResponseChallenge;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    public Challenge findById(Long id) {
        return challengeRepository.findById(id).orElseThrow(ChallengeNotFound::new);
    }

    public ResponseChallenge searchById(Long challengeId) {
        return challengeRepository.searchChallengeById(challengeId).orElseThrow(ChallengeNotFound::new);
    }

    /**
     * 기존 이미지들을 전부 삭제하고 업데이트 이미지들을 저장하는 로직
     */
    public Challenge updateChallenge(Long challengeId, RequestUpdateChallenge requestUpdateChallenge,
                                     List<MultipartFile> updateChallengeImgFiles, User user) {
        Challenge findChallenge = challengeRepository.findById(challengeId).orElseThrow(ChallengeNotFound::new);
        validateOwner(user, findChallenge);

        ChallengeEditor.ChallengeEditorBuilder editorBuilder = findChallenge.toEditor();
        ChallengeEditor challengeEditor = editorBuilder
                .title(requestUpdateChallenge.getTitle())
                .content(requestUpdateChallenge.getContent())
                .category(requestUpdateChallenge.getChallengeCategory())
                .build();

        findChallenge.update(challengeEditor);
        challengeImgService.updateChallengeImgs(findChallenge, updateChallengeImgFiles);

        return findChallenge;
    }

    public void deleteChallenge(Long challengeId, User user) {
        Challenge findChallenge = challengeRepository.findById(challengeId).orElseThrow(ChallengeNotFound::new);
        validateOwner(user, findChallenge);

        challengeRepository.delete(findChallenge);
    }

    private void validateOwner(User user, Challenge challenge) {
        if (!challenge.isOwner(user.getId())) {
            throw new AuthorizationException();
        }
    }

    @Transactional(readOnly = true)
    public Page<ResponseChallenge> searchChallengeByHashtag(String content, Pageable pageable){
        return challengeRepository.searchChallengeByHashtag(content, pageable);
    }

}
