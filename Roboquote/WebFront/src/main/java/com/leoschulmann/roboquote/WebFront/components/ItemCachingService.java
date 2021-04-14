package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.itemservice.entities.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@SessionScope
@RequiredArgsConstructor
public class ItemCachingService {

    private final HttpRestService httpService;
    private List<Item> cache;

    @PostConstruct
    public void init() {
        cache = new ArrayList<>();
        updateCache();
    }

    public List<Item> getItemsFromCache() {
        return cache;
    }

    public void updateCache() {
        cache = httpService.getAllItems();
    }
}
