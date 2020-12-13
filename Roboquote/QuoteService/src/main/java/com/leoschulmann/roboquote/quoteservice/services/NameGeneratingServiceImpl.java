package com.leoschulmann.roboquote.quoteservice.services;

import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.repositories.QuoteRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NameGeneratingServiceImpl implements NameGeneratingService {

    private final QuoteRepo quoteRepo;

    public NameGeneratingServiceImpl(QuoteRepo quoteRepo) {
        this.quoteRepo = quoteRepo;
    }

    private String getPrefix() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
    }

    private int getSuffix() {
        int i = 1;
        String prefix = getPrefix();
        while (quoteRepo.existsByNumber(prefix + i)) {
            i++;
        }
        return i;
    }

    @Override
    public String generate() {
        return getPrefix() + getSuffix();
    }

    @Override
    public Integer generateVer(String serial) {
        List<Quote> list = quoteRepo.findAllByNumber(serial);
        int maxver = list.stream().mapToInt(Quote::getVersion).max().orElse(0);
        return ++maxver;
    }
}
