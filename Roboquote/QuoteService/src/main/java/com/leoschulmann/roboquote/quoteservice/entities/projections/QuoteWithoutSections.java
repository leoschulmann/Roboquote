package com.leoschulmann.roboquote.quoteservice.entities.projections;

import org.javamoney.moneta.Money;

import java.time.LocalDate;

public interface QuoteWithoutSections {
    Integer getId();

    LocalDate getCreatedDate();

    String getSerialNumber();

    Integer getVersion();

    String getCustomer();

    String getDealer();

    Money getFinalPrice();
}
