package com.leoschulmann.roboquote.quoteservice.services;

public interface NameGeneratingService {
    String generate();

    Integer generateVer(String serial);
}
