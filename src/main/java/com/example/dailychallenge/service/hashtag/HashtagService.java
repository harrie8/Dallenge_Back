package com.example.dailychallenge.service.hashtag;

import com.example.dailychallenge.entity.hashtag.ChallengeHashtag;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.example.dailychallenge.exception.hashtag.HashTagNotFound;
import com.example.dailychallenge.repository.HashtagRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class HashtagService {
    private final HashtagRepository hashtagRepository;
    private final ChallengeHashtagService challengeHashtagService;

    public List<Hashtag> saveHashtag(List<String> hashtagDto){

        List<Hashtag> hashtags = new ArrayList<>();
        for (String tag : hashtagDto) {
            Hashtag hashtag = new Hashtag();
            try {                               // 이미 태그 내용이 존재하는 경우
                hashtag = hashtagRepository.findByContent(tag);
                hashtag.updateTagCount();
            } catch (NullPointerException e) {
                hashtag = Hashtag.builder()
                            .content(tag)
                            .build();
                hashtagRepository.save(hashtag);
            } finally {
                hashtags.add(hashtag);
            }
        }
        return hashtags;
    }

    public List<Hashtag> updateHashtag(List<String> hashtagDto, Long challengeId){
        List<ChallengeHashtag> challengeHashtags = challengeHashtagService.findByChallengeId(challengeId);
        List<Hashtag> savedTag = challengeHashtags.stream() // 기존 해시태그
                .map(ChallengeHashtag::getHashtag).collect(Collectors.toList());
        List<String> savedContent = savedTag.stream().map(Hashtag::getContent).collect(Collectors.toList());

        List<String> saveHashtagDto = new ArrayList<>();
        for (String tag : hashtagDto) {
            if(!savedContent.contains(tag)){ // 수정할 태그가 기존 태그들에 포함이 안되면
                saveHashtagDto.add(tag);
            }
        }
        List<Hashtag> res = this.saveHashtag(saveHashtagDto);

        for (String hashtag : savedContent) {
            if (!hashtagDto.contains(hashtag)) { // 기존 태그가 수정할 태그에 포함이 안되면
                Hashtag deleteHashtag = hashtagRepository.findByContent(hashtag);

                challengeHashtagService.deleteChallengeHashtag(challengeId, deleteHashtag.getId());

                if (deleteHashtag.getTagCount() > 1) {
                    deleteHashtag.minusTagCount();  // 1 빼기}
                } else {
                    hashtagRepository.delete(deleteHashtag); // 삭제
                }
            }
        }
        return res;

    }

    public void deleteHashtag(List<Hashtag> hashtags){
        for (Hashtag hashtag : hashtags) {
            Hashtag deleteHashtag = hashtagRepository.findById(hashtag.getId())
                    .orElseThrow(HashTagNotFound::new);
            if(deleteHashtag.getTagCount()>1) deleteHashtag.minusTagCount();  // 1 빼기
            else {
                hashtagRepository.delete(deleteHashtag); // 삭제
            }
        }
    }

    public List<Hashtag> searchThreeMostWrittenHashtags() {
        return hashtagRepository.findTop3ByOrderByTagCountDescContentAsc();
    }
}
