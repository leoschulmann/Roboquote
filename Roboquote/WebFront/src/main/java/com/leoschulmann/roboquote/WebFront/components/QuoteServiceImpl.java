package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class QuoteServiceImpl implements QuoteService {

    @Value("${quoteservice.url}")
    String url;

    @Override
    public List<Quote> findAll() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Quote[]> responseEntity = restTemplate.getForEntity(url, Quote[].class);
        if (responseEntity.getBody() != null) {
            return Arrays.asList(responseEntity.getBody());
        } else return new ArrayList<>();
    }
}
