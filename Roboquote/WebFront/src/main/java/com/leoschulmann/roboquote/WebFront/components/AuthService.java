package com.leoschulmann.roboquote.WebFront.components;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public interface AuthService {
    HttpEntity<String> provideHttpEntityWithCredentials();

    HttpHeaders provideHttpHeadersWithCredentials();
}
