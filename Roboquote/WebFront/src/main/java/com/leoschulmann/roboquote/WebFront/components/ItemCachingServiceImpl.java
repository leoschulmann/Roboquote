package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.itemservice.entities.Item;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@SessionScope
public class ItemCachingServiceImpl implements ItemCachingService {

    private final HttpRestService httpService;
    private List<Item> cache;

    public ItemCachingServiceImpl(HttpRestService httpService) {
        this.httpService = httpService;
    }

    @PostConstruct
    public void init() {
        cache = new ArrayList<>();
        updateCache();
    }

    @Override
    public List<Item> getItemsFromCache() {
        return cache;
    }

    @Override
    public void updateCache() {
        cache = httpService.getAllItems();
    }
}
