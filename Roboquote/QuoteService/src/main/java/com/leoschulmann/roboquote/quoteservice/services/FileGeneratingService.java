package com.leoschulmann.roboquote.quoteservice.services;

import com.leoschulmann.roboquote.quoteservice.entities.Quote;

public interface FileGeneratingService {

    byte[] generateFile(Quote quote);

    String getExtension();
}
