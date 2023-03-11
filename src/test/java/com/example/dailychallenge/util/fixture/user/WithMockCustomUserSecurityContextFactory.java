package com.example.dailychallenge.util.fixture.user;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithAuthUser> {

    @Override
    public SecurityContext createSecurityContext(WithAuthUser annotation) {
        final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        UserDetails principal = new org.springframework.security.core.userdetails.User(
                annotation.email(), annotation.password(),
                true, true, true, true,
                new ArrayList<>()
        );
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(annotation.roles()));
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        securityContext.setAuthentication(authentication);
        return securityContext;
    }
}
