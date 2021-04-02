package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.itemservice.dto.ItemDto;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@SessionScope
public class ItemCachingServiceImpl implements ItemCachingService {

    private final HttpRestService httpService;
    private List<Item> cache;
    private final DtoConverter dtoConverter;

    public ItemCachingServiceImpl(HttpRestService httpService, DtoConverter dtoConverter) {
        this.httpService = httpService;
        this.dtoConverter = dtoConverter;
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
        List<ItemDto> listDto = httpService.getAllItems();
        cache = listDto.stream().map(d -> dtoConverter.convertToItem(d)).collect(Collectors.toList());
    }
}
