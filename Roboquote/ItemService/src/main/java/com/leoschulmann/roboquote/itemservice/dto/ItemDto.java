package com.leoschulmann.roboquote.itemservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leoschulmann.roboquote.itemservice.validation.AnyValue;
import com.leoschulmann.roboquote.itemservice.validation.ValidDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Integer id;

    @Size(max = 255)
    @JsonProperty("part-number")
    private String partNumber;

    @Size(max = 255)
    private String brand;

    @NotBlank
    @Size(max = 2000)
    @JsonProperty("name-russian")
    private String nameRus;

    @NotBlank
    @Size(max = 2000)
    @JsonProperty("name-english")
    private String nameEng;

    @Max(100)
    @Min(-100)
    @JsonProperty("selling-margin")
    private Double sellingMargin;

    @AnyValue(values = {"EUR", "USD", "RUB", "JPY"})
    @JsonProperty("currency-buying")
    private String currencyBuying;

    @JsonProperty("amount-buying")
    private Double amountBuying;

    @AnyValue(values = {"EUR", "USD", "RUB", "JPY"})
    @JsonProperty("currency-selling")
    private String currencySelling;

    @JsonProperty("amount-selling")
    private Double amountSelling;

    @ValidDate(format = "yyyy-MM-dd")
    @JsonProperty("date-created")
    private String dateCreated;

    @ValidDate(format = "yyyy-MM-dd")
    @JsonProperty("date-modified")
    private String dateModified;

    @JsonProperty("overridden-sell-price")
    private Boolean overriddenSellPrice;
}
