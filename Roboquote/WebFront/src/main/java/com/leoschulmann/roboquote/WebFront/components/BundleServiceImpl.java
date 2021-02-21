package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.itemservice.dto.BundleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class BundleServiceImpl implements BundleService {
    @Value("${bundleservice.url}")
    private String url;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthService authService;

    @Override
    public List<BundleDto> getBundlesList() {
        HttpEntity<String> entity = authService.provideHttpEntityWithCredentials();
        ResponseEntity<BundleDto[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, BundleDto[].class);
        if (responseEntity.getBody() != null) {
            return Arrays.asList(responseEntity.getBody());
        } else return new ArrayList<>();
    }

    @Override
    public BundleDto getBundleById(int id) {
        HttpEntity<String> entity = authService.provideHttpEntityWithCredentials();
        ResponseEntity<BundleDto> responseEntity = restTemplate.exchange(url + id, HttpMethod.GET, entity, BundleDto.class);
        return responseEntity.getBody();
    }
}
