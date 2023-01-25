package com.example.dailychallenge.service;

import com.example.dailychallenge.entity.social.GoogleUser;
import com.example.dailychallenge.entity.ProviderUser;
import com.example.dailychallenge.entity.User;
import com.example.dailychallenge.repository.UserRepository;
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

    public void register(ProviderUser providerUser, OAuth2UserRequest userRequest) {

        User user = userRepository.findByEmail(providerUser.getEmail());

        if(user==null){ // db에 user 정보가 없는 경우, db에 저장
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            userService.saveSocialUser(registrationId,providerUser);
        }
    }

    public ProviderUser providerUser(ClientRegistration clientRegistration, OAuth2User oAuth2User) {
        String registrationId = clientRegistration.getRegistrationId();

        if (registrationId.equals("google")) { // 소셜 로그인 중 google 인 경우
            return new GoogleUser(oAuth2User, clientRegistration);
        }
        return null;
    }
}
