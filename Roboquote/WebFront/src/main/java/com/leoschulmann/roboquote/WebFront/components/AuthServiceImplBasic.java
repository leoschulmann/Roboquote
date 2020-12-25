package com.leoschulmann.roboquote.WebFront.components;

import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.base64.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.PostConstruct;

@Service
@SessionScope
public class AuthServiceImplBasic implements AuthService {

    private String n;
    private String p;

    @PostConstruct
    private void cacheAuth() {
        //unfortunately session credentials have to be cached, bc SCH sometimes returns
        //empty Authentication object, resulting in unexpected 401-s on authorized user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        n = authentication.getName();
        p = authentication.getCredentials().toString();
    }

    private String[] getAuthData() {
        byte[] bytes = (n + ":" + p).getBytes();
        String b64 = new String(Base64.encode(bytes));
        return new String[]{"Authorization", "Basic " + b64};
    }

    @Override
    public HttpEntity<String> provideHttpEntityWithCredentials() {
        return new HttpEntity<>(provideHttpHeadersWithCredentials());
    }

    @Override
    public HttpHeaders provideHttpHeadersWithCredentials() {
        HttpHeaders headers = new HttpHeaders();
        String[] creds = getAuthData();
        headers.add(creds[0], creds[1]);
        return headers;
    }
}
