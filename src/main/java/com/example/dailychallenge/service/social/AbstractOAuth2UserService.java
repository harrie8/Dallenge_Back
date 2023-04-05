package com.example.dailychallenge.service.social;

import com.example.dailychallenge.entity.social.GoogleUser;
import com.example.dailychallenge.entity.social.KakaoUser;
import com.example.dailychallenge.entity.social.ProviderUser;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.service.badge.UserBadgeEvaluationService;
import com.example.dailychallenge.service.badge.UserBadgeService;
import com.example.dailychallenge.service.users.UserService;
import java.util.Optional;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Getter
public abstract class AbstractOAuth2UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserBadgeService userBadgeService;
    @Autowired
    private UserBadgeEvaluationService userBadgeEvaluationService;

    public void register(ProviderUser providerUser, OAuth2UserRequest userRequest) {

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Optional<User> user = userRepository.findByEmail(providerUser.getEmail());

        if(user.isEmpty()){ // db에 user 정보가 없는 경우, db에 저장
            User savedUser = userService.saveSocialUser(registrationId, providerUser);
            userBadgeEvaluationService.createUserBadgeEvaluation(savedUser);
            userBadgeService.saveUserBadges(savedUser);
        }

    }

    public ProviderUser providerUser(ClientRegistration clientRegistration, OAuth2User oAuth2User) {
        String registrationId = clientRegistration.getRegistrationId();

        if (registrationId.equals("google")) { // 소셜 로그인 중 google 인 경우
            return new GoogleUser(oAuth2User, clientRegistration);
        } else if (registrationId.equals("kakao")) {
            return new KakaoUser(oAuth2User, clientRegistration);
        }
        return null;
    }
}
