package com.leoschulmann.roboquote.quoteservice.services;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class NameGeneratingServiceImplRandom implements NameGeneratingService {
    static Random random;

    static {
        random = new Random();
    }

    @Override
    public String generate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")) + random.nextInt(10);
    }
}
