package com.leoschulmann.roboquote.WebFront;

import com.leoschulmann.roboquote.itemservice.services.ItemBundleDtoConverter;
import com.leoschulmann.roboquote.quoteservice.services.QuoteDtoConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class WebFrontApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebFrontApplication.class, args);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    ItemBundleDtoConverter itemBundleDtoConverter() {
        return new ItemBundleDtoConverter();
    }

    @Bean
    QuoteDtoConverter quoteDtoConverter() {
        return new QuoteDtoConverter();
    }
}
