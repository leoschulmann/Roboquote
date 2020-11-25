package com.leoschulmann.roboquote.quoteservice.services;

import com.leoschulmann.roboquote.quoteservice.entities.Quote;

public interface ExcelService {

    byte[] generateFile(Quote quote);
}
