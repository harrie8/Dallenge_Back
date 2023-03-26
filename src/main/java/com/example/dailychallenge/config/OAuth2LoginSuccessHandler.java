package com.example.dailychallenge.config;

import com.example.dailychallenge.entity.social.GoogleUser;
import com.example.dailychallenge.entity.social.OAuth2ProviderUser;
import com.example.dailychallenge.entity.social.ProviderUser;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.users.UserNotFound;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.service.social.CustomOAuth2UserService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.utils.JwtTokenUtil;
import com.example.dailychallenge.vo.ResponseLoginUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenUtil jwtTokenUtil;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {

            DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();
            String email = (String) user.getAttributes().get("email");
            String accessToken = jwtTokenUtil.generateToken(email);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");

            User findUser = userService.findByEmail(email).orElseThrow(UserNotFound::new);
            ResponseLoginUser responseLoginUser = new ResponseLoginUser();
            responseLoginUser.setUserId(findUser.getId());
            responseLoginUser.setUserName(email);
            responseLoginUser.setToken(accessToken);

            String result = objectMapper.writeValueAsString(responseLoginUser);
            response.getWriter().write(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
