package com.leoschulmann.roboquote.WebFront.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DownloadServiceImpl implements DownloadService {

    @Value("${xlsxservice.url}")
    String downloadUrl;

    @Autowired
    AuthService authService;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public byte[] downloadXlsx(int id) {
        HttpHeaders headers = authService.provideHttpHeadersWithCredentials();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        HttpEntity<byte[]> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(
                downloadUrl + "/" + id, HttpMethod.GET, entity, byte[].class);

        return response.getBody();
    }

    @Override
    public String getExtension() {
        return ".xlsx";
    }
}
