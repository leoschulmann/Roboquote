package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.itemservice.entities.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Value("${itemservice.url}")
    String url;

    @Override
    public List<Item> findAll() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Item[]> responseEntity = restTemplate.getForEntity(url, Item[].class);
        if (responseEntity.getBody() != null) {
            return Arrays.asList(responseEntity.getBody());
        } else return new ArrayList<>();
    }
}

