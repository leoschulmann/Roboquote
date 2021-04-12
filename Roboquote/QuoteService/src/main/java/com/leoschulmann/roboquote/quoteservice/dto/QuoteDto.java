package com.leoschulmann.roboquote.quoteservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuoteDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer id;

    private String number;

    private String created;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String validThru;

    private Integer version;

    private String customer;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<QuoteSectionDto> sections;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer discount;

    private String dealer;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String customerInfo;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dealerInfo;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String paymentTerms;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String shippingTerms;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String warranty;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String installation;

    @Min(0)
    @Max(100)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer vat;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double eurRate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double usdRate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double jpyRate;

    @Max(100)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double conversionRate;

    private Double finalPriceAmount;

    private String finalPriceCurrency;


    public QuoteDto(Integer id, String number, String created, Integer version, String customer, String dealer,
                    double finalPriceAmount, String finalPriceCurrency) {
        this.id = id;
        this.number = number;
        this.created = created;
        this.version = version;
        this.customer = customer;
        this.dealer = dealer;
        this.finalPriceAmount = finalPriceAmount;
        this.finalPriceCurrency = finalPriceCurrency;
    }
}
