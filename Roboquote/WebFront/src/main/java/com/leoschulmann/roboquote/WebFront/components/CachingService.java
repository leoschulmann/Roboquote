package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.itemservice.entities.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@SessionScope
@RequiredArgsConstructor
public class CachingService {

    private final HttpRestService httpService;
    private List<Item> itemCache;
    private List<String> installationsCache;
    private List<String> paymentCache;
    private List<String> shippingCache;
    private List<String> warrantyCache;

    @PostConstruct
    public void init() {
        itemCache = new ArrayList<>();
        updateItemCache();
    }

    public List<Item> getItemsFromCache() {
        return itemCache;
    }

    public void updateItemCache() {
        itemCache = httpService.getAllItems();

        String[][] terms = httpService.getDistinctTerms();
        installationsCache = Arrays.asList(terms[0]);
        paymentCache = Arrays.asList(terms[1]);
        shippingCache = Arrays.asList(terms[2]);
        warrantyCache = Arrays.asList(terms[3]);
    }

    public List<String> getDistinctInstallationTerms() {
        return installationsCache;
    }


    public List<String> getDistinctPaymentTerms() {
        return paymentCache;
    }

    public List<String> getDistinctShippingTerms() {
        return shippingCache;
    }

    public List<String> getDistinctwarrantyTerms() {
        return warrantyCache;
    }
}
