package com.example.dailychallenge.entity.social;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Map;

public interface ProviderUser {

    String getId();
    String getUserName();
    String getPassword(); // 랜덤으로 만들거라서 의미는 없음
    String getEmail();
    String getProvider(); // 서비스 제공자
    List<? extends GrantedAuthority> getAuthorities();
    Map<String,Object> getAttributes();

}
