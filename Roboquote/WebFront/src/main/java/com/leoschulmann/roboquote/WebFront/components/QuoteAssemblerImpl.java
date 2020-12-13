package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class QuoteAssemblerImpl implements QuoteAssembler {
    @Value("${namingservice.url}")
    String getNameUrl;

    @Value("${quoteservice.save.url}")
    String saveQuoteUrl;

    @Override
    public int postNew(Quote quote) {
        if (quote.getNumber() == null) quote.setNumber(getNameFromService());
        if (quote.getVersion() == null) quote.setVersion(getVersionFromService(quote.getNumber()));
        return postQuote(quote);
    }

    private Integer getVersionFromService(String number) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Integer> responseEntity = restTemplate.getForEntity(getNameUrl + "/" + number, Integer.class);
        return responseEntity.getBody();
    }

    private String getNameFromService() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(getNameUrl, String.class);
        return responseEntity.getBody(); //todo exception handling
    }

    private int postQuote(Quote quote) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Quote> responseEntity = restTemplate.postForEntity(saveQuoteUrl, quote, Quote.class);
        return responseEntity.getBody().getId();
    }
}
