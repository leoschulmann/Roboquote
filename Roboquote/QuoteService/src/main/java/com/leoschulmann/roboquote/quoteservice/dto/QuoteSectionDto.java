package com.leoschulmann.roboquote.quoteservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuoteSectionDto {
    private int id;

    private List<ItemPositionDto> positions;

    private String name;

    private String discount;

    private Double totalAmount;

    private String totalCurrency;

}
