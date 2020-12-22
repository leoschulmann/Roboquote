package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Value("${namingservice.url}")
    String getNameUrl;

    @Value("${quoteservice.save.url}")
    String saveQuoteUrl;

    @Autowired
    RestTemplate restTemplate;

    final InventoryItemToItemPositionConverter itemConverter;

    public QuoteServiceImpl(InventoryItemToItemPositionConverter itemConverter) {
        this.itemConverter = itemConverter;
    }

    @Override
    public List<Quote> findAll() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Quote[]> responseEntity = restTemplate.getForEntity(url, Quote[].class);
        if (responseEntity.getBody() != null) {
            return Arrays.asList(responseEntity.getBody());
        } else return new ArrayList<>();
    }

    @Override
    public Quote createNewVersion(Quote source) {
        //everything except ipos's, version and dates
        Quote quote = new Quote(source.getNumber(), null, source.getCustomer(), source.getCustomerInfo(),
                source.getDealer(), source.getDealerInfo(), source.getPaymentTerms(), source.getShippingTerms(),
                source.getWarranty(), source.getInstallation(), source.getVat(), source.getDiscount(),
                source.getEurRate(), source.getUsdRate(), source.getJpyRate(), source.getConversionRate());

        getSectionsCopy(source, quote);
        return quote;
    }

    @Override
    public Quote createNewFromTemplate(Quote source) {
        Quote quote = new Quote(null, null, null, null, null, null, source.getPaymentTerms(), source.getShippingTerms(),
                source.getWarranty(), source.getInstallation(), source.getVat(), source.getDiscount(),
                source.getEurRate(), source.getUsdRate(), source.getJpyRate(), source.getConversionRate());

        getSectionsCopy(source, quote);
        return quote;
    }

    @Override
    public void addSections(Quote quote, QuoteSection qs) {
        quote.addSections(qs);
    }

    private void getSectionsCopy(Quote source, Quote quote) {
        for (QuoteSection sourceSection : source.getSections()) {
            QuoteSection section = new QuoteSection(sourceSection.getName());
            section.setDiscount(sourceSection.getDiscount());
            for (ItemPosition sourceIPos : sourceSection.getPositions()) {
                //todo make service convert all ip's in one request
                section.getPositions().add(
                        itemConverter.createItemPositionByItemId(sourceIPos.getItemId(), sourceIPos.getQty()));
            }
            quote.addSections(section);
        }
    }

    @Override
    public int postNew(Quote quote) {
        if (quote.getNumber() == null) quote.setNumber(getNameFromService());
        if (quote.getVersion() == null) quote.setVersion(getVersionFromService(quote.getNumber()));
        return postQuote(quote);
    }

    private Integer getVersionFromService(String number) {
        ResponseEntity<Integer> responseEntity = restTemplate.getForEntity(getNameUrl + "/" + number, Integer.class);
        return responseEntity.getBody();
    }

    private String getNameFromService() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(getNameUrl, String.class);
        return responseEntity.getBody(); //todo exception handling
    }

    private int postQuote(Quote quote) {
        ResponseEntity<Quote> responseEntity = restTemplate.postForEntity(saveQuoteUrl, quote, Quote.class);
        return responseEntity.getBody().getId();
    }

}
