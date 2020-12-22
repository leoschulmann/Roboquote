package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.itemservice.entities.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Value("${itemservice.url}")
    String url;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public List<Item> findAll() {
        ResponseEntity<Item[]> responseEntity = restTemplate.getForEntity(url, Item[].class);
        if (responseEntity.getBody() != null) {
            return Arrays.asList(responseEntity.getBody());
        } else return new ArrayList<>();
    }

    @Override
    public void saveItem(Item item) {
        item.setModified(LocalDate.now());
        item.setCreated(LocalDate.now());
        ResponseEntity<Item> responseEntity = restTemplate.postForEntity(url, item, Item.class);
    }

    @Override
    public void deleteItem(Item item) {
        int id = item.getId();
        restTemplate.delete(url + id);
    }

    @Override
    public void updateItem(Item item) {
        item.setModified(LocalDate.now());
        HttpEntity<Item> httpEntity = new HttpEntity<>(item, new HttpHeaders());
        ResponseEntity<Item> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, Item.class);
    }
}

