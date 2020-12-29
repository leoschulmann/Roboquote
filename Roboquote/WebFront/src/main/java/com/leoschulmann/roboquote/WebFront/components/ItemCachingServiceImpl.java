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

    final ItemService itemService;

    private List<Item> cache;

    public ItemCachingServiceImpl(ItemService itemService) {
        this.itemService = itemService;
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
        cache = itemService.findAll();
    }
}
