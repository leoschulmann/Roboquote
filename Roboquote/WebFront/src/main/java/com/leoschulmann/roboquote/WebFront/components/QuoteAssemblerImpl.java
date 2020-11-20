package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.WebFront.pojo.QuoteDetails;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class QuoteAssemblerImpl implements QuoteAssembler {
    @Value("${namingservice.url}")
    String getNameUrl;

    @Value("${quoteservice.save.url}")
    String saveQuoteUrl;


    @Override
    public void assembleAndPostNew(QuoteDetails details, List<QuoteSection> sections) {
        Quote q = new Quote(getNameFromService(), details.getValidThru(), details.getCustomer(), details.getDealer(),
                details.getCustomerInfo(), details.getDealerInfo());
        q.setVersion(1);
        q.setShippingTerms(details.getShippingTerms());
        q.setPaymentTerms(details.getPaymentTerms());
        q.setWarranty(details.getWarranty());
        q.setDiscount(details.getDiscount());
        sections.forEach(sect -> sect.getPositions().forEach(pos -> pos.setSection(sect)));
        sections.forEach(q::addSections);
        postQuote(q);
    }

    private String getNameFromService() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(getNameUrl, String.class);
        return responseEntity.getBody(); //todo exception handling
    }

    private void postQuote(Quote quote) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Quote> responseEntity = restTemplate.postForEntity(saveQuoteUrl, quote, Quote.class);
    }
}
