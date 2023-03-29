package com.example.dailychallenge.config;

import com.example.dailychallenge.config.filters.JwtExceptionFilter;
import com.example.dailychallenge.config.filters.JwtRequestFilter;
import com.example.dailychallenge.service.social.CustomOAuth2UserService;
import com.example.dailychallenge.service.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final JwtRequestFilter jwtRequestFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final AuthenticationEntryPointImpl authenticationEntryPointImpl;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override // 정적페이지 인증 X
    public void configure(WebSecurity web){
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override // 권한
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.formLogin().disable();

        http.headers().frameOptions().sameOrigin();

        http.authorizeRequests()
                .antMatchers("/h2-console/**").permitAll();

        http.authorizeRequests()
                .antMatchers("/images/**").permitAll(); // 이미지 불러오기

        http.
                authorizeRequests()
                .antMatchers(HttpMethod.GET, "/challenge/{challengeId:[\\d+]}").permitAll()
                .antMatchers(HttpMethod.GET, "/badges").permitAll()
                .antMatchers(HttpMethod.POST, "/user/resetPassword").permitAll()
                .antMatchers("/user/{userId:[\\d+]}/**", "/user/challenge", "/user/participate", "/user/done",
                        "/challenge/new", "/challenge/{challengeId:[\\d+]}/**",
                        "/{challengeId:[\\d+]}/comment/new", "/{challengeId:[\\d+]}/comment/{\\d+}",
                        "/{commentId:[\\d+]}/like",
                        "/{challengeId:[\\d+]}/bookmark/new", "/user/badges").authenticated()
//                .antMatchers("/user/login","/user/new","/**.html","/images/**","/","/token/**","/login/**","/oauth2/**","/api/user").permitAll()
                .anyRequest().permitAll()
                .and()
                .oauth2Login()
                .successHandler(oAuth2LoginSuccessHandler)
//                .defaultSuccessUrl("/")
                .userInfoEndpoint()
                .userService(customOAuth2UserService);

        http
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPointImpl)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtRequestFilter.class);

        http.logout().logoutSuccessUrl("/");
        http.cors().configurationSource(corsConfigurationSource());
    }

    @Override // 인증
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
