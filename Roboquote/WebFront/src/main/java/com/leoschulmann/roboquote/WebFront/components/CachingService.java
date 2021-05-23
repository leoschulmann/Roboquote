package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.quoteservice.dto.DistinctTermsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
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
    public void init() throws ServerCommunicationException {
        itemCache = new ArrayList<>();
        updateItemCache();
    }

    public List<Item> getItemsFromCache() {
        return itemCache;
    }

    public void updateItemCache() throws ServerCommunicationException {
        itemCache = httpService.getAllItems();
    }

    public void updateTermsCache() {
        DistinctTermsDto terms = httpService.getDistinctTerms();
        installationsCache = terms.getInstallationTerms();
        paymentCache = terms.getPaymentTerms();
        shippingCache = terms.getShippingTerms();
        warrantyCache = terms.getWarranty();
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
