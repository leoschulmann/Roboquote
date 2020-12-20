package com.leoschulmann.roboquote.WebFront.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

//@Service  //unused
public class AuthenticationProviderImpl implements AuthenticationProvider {
    private final UserDao userDao;
    private Authentication authentication;

    public AuthenticationProviderImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        this.authentication = authentication;
        String login = authentication.getName();
        String pass = authentication.getCredentials().toString();

        Optional<User> opt = userDao.getByUserName(login);
        User user = opt.orElseThrow(() -> {
            throw new BadCredentialsException(login + " does not exist");
        });
        if (!pass.equals(user.getPassword())) {
            throw new BadCredentialsException(pass + " does not match");
        }

        UserDetails principal = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
        return new UsernamePasswordAuthenticationToken(principal, pass, principal.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
