package com.leoschulmann.roboquote.WebFront.components;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class DownloadService {

    @Value("${xlsxservice.url}")
    String downloadUrl;

    private final AuthService authService;

    private final RestTemplate restTemplate;

    public byte[] downloadXlsx(int id) {
        HttpHeaders headers = authService.provideHttpHeadersWithCredentials();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        HttpEntity<byte[]> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(
                downloadUrl + "/" + id, HttpMethod.GET, entity, byte[].class);

        return response.getBody();
    }

    public String getExtension() {
        return ".xlsx";
    }
}
