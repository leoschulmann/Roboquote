package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.itemservice.dto.BundleDto;
import com.leoschulmann.roboquote.itemservice.dto.PostitionDto;
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

    @Override
    public List<BundleDto> getBundlesList() {
        HttpEntity<String> entity = authService.provideHttpEntityWithCredentials();
        ResponseEntity<BundleDto[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, BundleDto[].class);
        if (responseEntity.getBody() != null) {
            return Arrays.asList(responseEntity.getBody());
        } else return new ArrayList<>();
    }

    @Override
    public BundleDto getBundleById(int id) {
        HttpEntity<String> entity = authService.provideHttpEntityWithCredentials();
        ResponseEntity<BundleDto> responseEntity = restTemplate.exchange(url + id, HttpMethod.GET, entity, BundleDto.class);
        return responseEntity.getBody();
    }

    @Override
    public BundledPosition convertToBundlePostion(Item item) {
        return new BundledPosition(1, item);
    }

    @Transactional
    @Override
    public void saveBundle(Bundle bundle) {
        BundleDto dto = convertToDto(bundle);
        HttpHeaders headers = authService.provideHttpHeadersWithCredentials();
        HttpEntity<BundleDto> entity = new HttpEntity<>(dto, headers);
        restTemplate.exchange(url, HttpMethod.POST, entity, Item.class);
    }

    private BundleDto convertToDto(Bundle b) {
        List<PostitionDto> postitions = b.getPositions().stream()
                .map(bp -> new PostitionDto(bp.getItem().getId(), bp.getQty(), bp.getItem().getNameRus()))
                .collect(Collectors.toList());
        return new BundleDto(b.getNameRus(), postitions);
    }

}
