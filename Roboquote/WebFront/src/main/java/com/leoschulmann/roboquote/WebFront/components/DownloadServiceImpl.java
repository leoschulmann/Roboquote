package com.leoschulmann.roboquote.WebFront.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DownloadServiceImpl implements DownloadService {

    @Value("${xlsxservice.url}")
    String downloadUrl;

    @Override
    public byte[] downloadXlsx(int id) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                downloadUrl + "/" + id, HttpMethod.GET, new HttpEntity<>(headers), byte[].class);

        return response.getBody();
    }


}
