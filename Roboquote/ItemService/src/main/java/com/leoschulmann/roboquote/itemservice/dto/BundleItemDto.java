package com.leoschulmann.roboquote.itemservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.leoschulmann.roboquote.itemservice.validation.ExistingItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BundleItemDto {

    @ExistingItem
    private int itemId;

    @Min(1) @Max(100)
    private int qty;

    @Size(min = 1, max = 2000)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;

    public BundleItemDto(int itemId, int qty) {
        this.itemId = itemId;
        this.qty = qty;
    }
}
