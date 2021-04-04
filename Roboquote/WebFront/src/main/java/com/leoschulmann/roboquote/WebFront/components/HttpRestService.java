package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.itemservice.dto.BundleDto;
import com.leoschulmann.roboquote.itemservice.dto.BundleItemDto;
import com.leoschulmann.roboquote.itemservice.dto.ItemDto;
import com.leoschulmann.roboquote.itemservice.dto.ItemId;
import com.leoschulmann.roboquote.itemservice.entities.Bundle;
import com.leoschulmann.roboquote.itemservice.entities.BundledPosition;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.itemservice.services.ItemBundleDtoConverter;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.services.QuoteDtoConverter;
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
    private final ItemBundleDtoConverter itemBundleDtoConverter;
    private final QuoteDtoConverter quoteDtoConverter;

    public List<Item> getAllItems() {
        RequestEntity<Void> request = RequestEntity.get(URI.create(itemUrl + "new"))         //todo remove 'new'
                .headers(auth.provideHttpHeadersWithCredentials()).accept(MediaType.APPLICATION_JSON).build();
        ItemDto[] arr = restTemplate.exchange(request, ItemDto[].class).getBody();
        if (arr == null || arr.length == 0) throw new RuntimeException("work in progress!"); //todo replace stub

        return Arrays.stream(arr).map(itemBundleDtoConverter::convertToItem).collect(Collectors.toList());
    }

    public Item getItem(int id) {
        //todo remove 'new'
        RequestEntity<Void> request = RequestEntity.get(URI.create(itemUrl + "new/" + id))
                .headers(auth.provideHttpHeadersWithCredentials()).accept(MediaType.APPLICATION_JSON).build();
        ItemDto dto = restTemplate.exchange(request, ItemDto.class).getBody();
        return itemBundleDtoConverter.convertToItem(dto); //todo handle nullable
    }

    public List<Item> getItemsByIds(int... ids) {
        List<ItemId> idList = Arrays.stream(ids).mapToObj(ItemId::new).collect(Collectors.toList());
        RequestEntity<List<ItemId>> request = RequestEntity.post(URI.create(itemUrl + "new/multiple")) //todo 'new'
                .headers(auth.provideHttpHeadersWithCredentials()).accept(MediaType.APPLICATION_JSON).body(idList);
        ItemDto[] arr = restTemplate.exchange(request, ItemDto[].class).getBody();
        if (arr == null || arr.length == 0) throw new RuntimeException("work in progress!"); //todo replace stub
        return Arrays.stream(arr).map(itemBundleDtoConverter::convertToItem).collect(Collectors.toList());
    }

    @Transactional
    public void saveItem(Item item) { //todo return id???
        ItemDto dto = itemBundleDtoConverter.convertToItemDto(item);
        RequestEntity<ItemDto> request = RequestEntity.post(URI.create(itemUrl + "new/")) //todo 'new'
                .headers(auth.provideHttpHeadersWithCredentials()).body(dto);
        restTemplate.exchange(request, Object.class);
    }

    public void deleteItem(int id) {
        RequestEntity<Void> request = RequestEntity.delete(URI.create(itemUrl + "new/" + id)) //todo 'new'
                .headers(auth.provideHttpHeadersWithCredentials()).build();
        restTemplate.exchange(request, Object.class);
    }

    @Transactional
    public void updateItem(Item item) {
        int id = item.getId();
        ItemDto dto = itemBundleDtoConverter.convertToItemDto(item);
        RequestEntity<ItemDto> request = RequestEntity.put(URI.create(itemUrl + "new/" + id)) //todo 'new'
                .headers(auth.provideHttpHeadersWithCredentials()).body(dto);
        restTemplate.exchange(request, Object.class);
    }

    public List<Bundle> getAllBundlesNamesAndIds() {
        RequestEntity<Void> request = RequestEntity.get(URI.create(bundleUrl))
                .headers(auth.provideHttpHeadersWithCredentials()).accept(MediaType.APPLICATION_JSON).build();
        BundleDto[] arr = restTemplate.exchange(request, BundleDto[].class).getBody();
        if (arr == null || arr.length == 0) throw new RuntimeException("work in progress!"); //todo replace stub

        return Arrays.stream(arr).map(dto -> {
            Bundle b = new Bundle();
            b.setNameRus(dto.getName());
            b.setId(dto.getId());
            return b;
        }).collect(Collectors.toList());

    }

    public Bundle getBundleById(int id) {
        RequestEntity<Void> request = RequestEntity.get(URI.create(bundleUrl + id))
                .headers(auth.provideHttpHeadersWithCredentials()).accept(MediaType.APPLICATION_JSON).build();
        BundleDto dto = restTemplate.exchange(request, BundleDto.class).getBody();
        if (dto == null) throw new RuntimeException("work in progress!"); //todo replace stub
        Bundle bundle = new Bundle();
        bundle.setId(dto.getId());
        bundle.setNameRus(dto.getName()); //todo i8n

        int[] itemIds = dto.getItems().stream().mapToInt(BundleItemDto::getItemId).toArray();
        List<Item> items = this.getItemsByIds(itemIds);
        List<BundledPosition> positions = items.stream().map(i -> {
            int qty = dto.getItems().stream().filter(idto -> idto.getItemId() == i.getId()).findAny().get().getQty();
            return new BundledPosition(qty, i);
        }).collect(Collectors.toList());
        bundle.setPositions(positions);
        return bundle;
    }

    public void saveBundle(Bundle bundle) { //todo return id???
        BundleDto dto = itemBundleDtoConverter.convertFromBundle(bundle);
        RequestEntity<BundleDto> request = RequestEntity.post(URI.create(bundleUrl))
                .headers(auth.provideHttpHeadersWithCredentials()).body(dto);
        restTemplate.exchange(request, Object.class);
    }

    public void updateBundle(Bundle bundle) {
        BundleDto dto = itemBundleDtoConverter.convertFromBundle(bundle);
        RequestEntity<BundleDto> request = RequestEntity.put(URI.create(bundleUrl + bundle.getId()))
                .headers(auth.provideHttpHeadersWithCredentials()).body(dto);
        restTemplate.exchange(request, Object.class);
    }

    public void deleteBundle(int id) {
        RequestEntity<Void> request = RequestEntity.delete(URI.create(bundleUrl + id))
                .headers(auth.provideHttpHeadersWithCredentials()).build();
        restTemplate.exchange(request, Object.class);
    }

    public ItemPosition convertBundledPositionToItemPosition(BundledPosition bp) { //todo make some converter service
        return new ItemPosition(bp.getItem().getNameRus(), bp.getItem().getPartno(), bp.getItem().getSellingPrice(),
                bp.getQty(), bp.getItem().getId());
    }

    public BundledPosition convertToBundlePosition(Item item) {
        return new BundledPosition(1, item);
    }

    public ItemPosition convertItemToItemPosition(Item item) {
        return new ItemPosition(item.getNameRus(), item.getPartno(), item.getSellingPrice(), 1, item.getId());
    }

    public List<ItemPosition> batchCopyPositions(List<ItemPosition> positions) {
        List<Item> items = getItemsByIds(positions.stream().mapToInt(ItemPosition::getItemId).toArray());

        return items.stream().map(i -> {
            int qty = positions.stream().filter(p -> p.getItemId() == i.getId()).findAny().get().getQty();
            ItemPosition ip = convertItemToItemPosition(i);
            ip.setQty(qty);
            return ip;
        }).collect(Collectors.toList());
    }
}
