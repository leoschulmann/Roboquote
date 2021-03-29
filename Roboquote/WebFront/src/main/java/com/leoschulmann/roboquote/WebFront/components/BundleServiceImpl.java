package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.itemservice.dto.BundleDto;
import com.leoschulmann.roboquote.itemservice.dto.BundleItemDto;
import com.leoschulmann.roboquote.itemservice.entities.Bundle;
import com.leoschulmann.roboquote.itemservice.entities.BundledPosition;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BundleServiceImpl implements BundleService {
    @Value("${bundleservice.url}")
    private String url;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthService authService;

    @Autowired
    ItemService itemService;

    @Override
    public List<BundleDto> getBundlesList() {
        HttpEntity<String> entity = authService.provideHttpEntityWithCredentials();
        ResponseEntity<BundleDto[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, BundleDto[].class);
        if (responseEntity.getBody() != null) {
            return Arrays.asList(responseEntity.getBody());
        } else return new ArrayList<>();
    }

    @Override
    public BundleDto getBundleDtoById(int id) {
        HttpEntity<String> entity = authService.provideHttpEntityWithCredentials();
        ResponseEntity<BundleDto> responseEntity = restTemplate.exchange(url + id, HttpMethod.GET, entity, BundleDto.class);
        return responseEntity.getBody();
    }

    @Override
    public BundledPosition convertToBundlePosition(Item item) {
        return new BundledPosition(1, item);
    }

    @Transactional
    @Override
    public void saveBundle(Bundle bundle) {
        BundleDto dto = convertToDto(bundle);
        HttpHeaders headers = authService.provideHttpHeadersWithCredentials();
        HttpEntity<BundleDto> entity = new HttpEntity<>(dto, headers);
        restTemplate.exchange(url, HttpMethod.POST, entity, BundleDto.class);
    }

    @Override
    public Bundle convertToBundle(BundleDto dto) {
        Bundle b = new Bundle();
        b.setId(dto.getId());
        b.setNameRus(dto.getName());
        b.setNameEng(dto.getName());
        b.setPositions(dto.getItems().stream().map(posDto -> new BundledPosition(posDto.getQty(),
                itemService.getById(posDto.getItemId()))).collect(Collectors.toList())); //todo might generate too many queries
        return b;
    }

    @Override
    public void updateBundle(Bundle bundle) {
        BundleDto dto = convertToDto(bundle);
        HttpHeaders headers = authService.provideHttpHeadersWithCredentials();
        HttpEntity<BundleDto> entity = new HttpEntity<>(dto, headers);
        restTemplate.exchange(url + bundle.getId(), HttpMethod.PUT, entity, BundleDto.class);
    }

    @Override
    public void deleteBundle(int id) {
        HttpHeaders headers = authService.provideHttpHeadersWithCredentials();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url + id, HttpMethod.DELETE, entity, BundleDto[].class);
    }

    @Override
    public Bundle getBundleById(int id) {
        BundleDto dto = getBundleDtoById(id);
        return convertToBundle(dto);
    }

    private BundleDto convertToDto(Bundle b) {
        List<BundleItemDto> postitions = b.getPositions().stream()
                .map(bp -> new BundleItemDto(bp.getItem().getId(), bp.getQty(), bp.getItem().getNameRus()))
                .collect(Collectors.toList());
        return new BundleDto(b.getNameRus(), postitions);
    }

}
