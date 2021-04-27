package com.leoschulmann.roboquote.quoteservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DistinctTermsDto {
    private List<String> installationTerms;
    private List<String> paymentTerms;
    private List<String> shippingTerms;
    private List<String> warranty;
}