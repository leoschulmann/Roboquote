package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.itemservice.dto.BundleDto;
import com.leoschulmann.roboquote.itemservice.dto.ItemDto;
import com.leoschulmann.roboquote.itemservice.dto.ItemId;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HttpRestService {

    @Value("${itemservice.url}")
    private String itemUrl;

    @Value("${bundleservice.url}")
    private String bundleUrl;

    @Value("${quoteservice.url}")
    private String quoteUrl;

    @Value("${namingservice.url}")
    private String nameUrl;

    private final RestTemplate restTemplate; //todo register with responseErrorHandler
    private final AuthService auth;

    public List<ItemDto> getAllItems() {
        RequestEntity<Void> request = RequestEntity.get(URI.create(itemUrl + "new"))
                .headers(auth.provideHttpHeadersWithCredentials()).accept(MediaType.APPLICATION_JSON).build();
        ItemDto[] arr = restTemplate.exchange(request, ItemDto[].class).getBody();
        if (arr == null || arr.length == 0) throw new RuntimeException("work in progress!"); //todo replace stub
        return Arrays.asList(arr);
    }

    public ItemDto getItem(int id) {
        //todo remove 'new'
        RequestEntity<Void> request = RequestEntity.get(URI.create(itemUrl + "new/" + id))
                .headers(auth.provideHttpHeadersWithCredentials()).accept(MediaType.APPLICATION_JSON).build();
        return restTemplate.exchange(request, ItemDto.class).getBody();
    }

    public List<ItemDto> getItemsByIds(int... ids) {
        List<ItemId> idList = Arrays.stream(ids).mapToObj(ItemId::new).collect(Collectors.toList());
        RequestEntity<List<ItemId>> request = RequestEntity.post(URI.create(itemUrl + "new/multiple")) //todo 'new'
                .headers(auth.provideHttpHeadersWithCredentials()).accept(MediaType.APPLICATION_JSON).body(idList);
        ItemDto[] arr = restTemplate.exchange(request, ItemDto[].class).getBody();
        if (arr == null || arr.length == 0) throw new RuntimeException("work in progress!"); //todo replace stub
        return Arrays.asList(arr);
    }

    @Transactional
    public void saveItem(ItemDto item) { //todo return id???
        RequestEntity<ItemDto> request = RequestEntity.post(URI.create(itemUrl + "new/"))
                .headers(auth.provideHttpHeadersWithCredentials()).body(item);
        restTemplate.exchange(request, Object.class);
    }

    public void deleteItem(int id) {
        RequestEntity<Void> request = RequestEntity.delete(URI.create(itemUrl + "new/" + id))
                .headers(auth.provideHttpHeadersWithCredentials()).build();
        restTemplate.exchange(request, Object.class);
    }

    @Transactional
    public void updateItem(ItemDto item) {
        int id = item.getId();
        RequestEntity<ItemDto> request = RequestEntity.put(URI.create(itemUrl + "new/" + id))
                .headers(auth.provideHttpHeadersWithCredentials()).body(item);
        restTemplate.exchange(request, Object.class);
    }

    List<BundleDto> getAllBundles() {
        return null;
    }

    BundleDto getBundle(int id) {
        return null;
    }

    void saveBundle(BundleDto bundle) { //todo return id???
    }

    void updateBundle(BundleDto bundle) {
    }

    void deleteBundle(int id) {
    }
}
