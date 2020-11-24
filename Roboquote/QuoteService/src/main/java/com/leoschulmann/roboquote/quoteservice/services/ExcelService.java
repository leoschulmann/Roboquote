package com.leoschulmann.roboquote.quoteservice.services;

import com.leoschulmann.roboquote.quoteservice.entities.Quote;

import java.io.IOException;

public interface ExcelService {

    byte[] generateFile(Quote quote) throws IOException;
}
