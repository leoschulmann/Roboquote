package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.itemservice.entities.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    AuthService authService;

    @Override
    public List<Item> findAll() {
        HttpEntity<String> entity = authService.provideHttpEntityWithCredentials();
        ResponseEntity<Item[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Item[].class);
        if (responseEntity.getBody() != null) {
            return Arrays.asList(responseEntity.getBody());
        } else return new ArrayList<>();
    }

    @Override
    public Item getById(int id) {
        HttpEntity<String> entity = authService.provideHttpEntityWithCredentials();
        ResponseEntity<Item> responseEntity = restTemplate.exchange(url + id, HttpMethod.GET, entity, Item.class);
        return responseEntity.getBody();
    }

    @Transactional
    @Override
    public void saveItem(Item item) {
        item.setModified(LocalDate.now());
        item.setCreated(LocalDate.now());
        HttpHeaders headers = authService.provideHttpHeadersWithCredentials();
        HttpEntity<Item> entity = new HttpEntity<>(item, headers);
        restTemplate.exchange(url, HttpMethod.POST, entity, Item.class);
    }

    @Transactional
    @Override
    public void deleteItem(Item item) {
        int id = item.getId();
        HttpEntity<String> entity = authService.provideHttpEntityWithCredentials();
        restTemplate.exchange(url + id, HttpMethod.DELETE, entity, Item.class);
    }

    @Transactional
    @Override
    public void updateItem(Item item) {
        item.setModified(LocalDate.now());
        HttpHeaders headers = authService.provideHttpHeadersWithCredentials();
        HttpEntity<Item> entity = new HttpEntity<>(item, headers);
        restTemplate.exchange(url, HttpMethod.PUT, entity, Item.class);
    }
}

