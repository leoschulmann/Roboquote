package com.leoschulmann.roboquote.WebFront.components;

import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.base64.Base64;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImplBasic implements AuthService {
    @Override
    public String[] getAuthData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String pass = authentication.getCredentials().toString();
        String user = authentication.getName();

        byte[] bytes = (user + ":" + pass).getBytes();
        String b64 = new String(Base64.encode(bytes));
        return new String[]{"Authorization", "Basic " + b64};
    }
}
