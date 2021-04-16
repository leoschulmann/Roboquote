package com.leoschulmann.roboquote.quoteservice.entities.projections;

import org.javamoney.moneta.Money;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface QuoteWithoutSections {
    Integer getId();

    LocalDate getCreatedDate();

    LocalDateTime getCreatedDateTime();

    String getSerialNumber();

    Integer getVersion();

    String getCustomer();

    String getDealer();

    Money getFinalPrice();
}
