package com.example.dailychallenge.entity.social;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class KakaoUser extends OAuth2ProviderUser{

    public KakaoUser(OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        super(oAuth2User.getAttributes(),oAuth2User, clientRegistration);
    }

    @Override
    public String getId() { // 식별자
        return (String)getAttributes().get("id");
    }

    @Override
    public String getUserName() { // 유저 닉네임
        return (String)getAttributes().get("nickname");
    }

}

