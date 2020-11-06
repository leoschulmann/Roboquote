package com.leoschulmann.roboquote.quoteservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.properties")

public class QuoteserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuoteserviceApplication.class, args);
    }
}
