package com.leoschulmann.roboquote.quoteservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemPositionDto {

    private Integer id;

    private String name;

    private String partNo;

    private Double sellingAmount;

    private String sellingCurrency;

    private Integer qty;

    private Integer itemId;
}
