package com.leoschulmann.roboquote.WebFront.pojo;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.time.LocalDate;

public class QuoteDetails {
    private String name;
    private String customer;
    private String customerInfo;
    private String dealer;
    private String dealerInfo;
    private String paymentTerms;
    private String shippingTerms;
    private String warranty;
    private LocalDate validThru;
    private Integer discount;
    private Integer vat;
    private double conversionRate;
    private BigDecimal eurRate;
    private BigDecimal usdRate;
    private BigDecimal jpyRate;
    private MonetaryAmount finalPrice;

    public QuoteDetails() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getDealer() {
        return dealer;
    }

    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    public String getDealerInfo() {
        return dealerInfo;
    }

    public void setDealerInfo(String dealerInfo) {
        this.dealerInfo = dealerInfo;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public String getShippingTerms() {
        return shippingTerms;
    }

    public void setShippingTerms(String shippingTerms) {
        this.shippingTerms = shippingTerms;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public LocalDate getValidThru() {
        return validThru;
    }

    public void setValidThru(LocalDate validThru) {
        this.validThru = validThru;
    }

    public String getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(String customerInfo) {
        this.customerInfo = customerInfo;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Integer getVat() {
        return vat;
    }

    public void setVat(Integer vat) {
        this.vat = vat;
    }

    public double getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(double conversionRate) {
        this.conversionRate = conversionRate;
    }

    public BigDecimal getEurRate() {
        return eurRate;
    }

    public void setEurRate(BigDecimal eurRate) {
        this.eurRate = eurRate;
    }

    public BigDecimal getUsdRate() {
        return usdRate;
    }

    public void setUsdRate(BigDecimal usdRate) {
        this.usdRate = usdRate;
    }

    public BigDecimal getJpyRate() {
        return jpyRate;
    }

    public void setJpyRate(BigDecimal jpyRate) {
        this.jpyRate = jpyRate;
    }

    public MonetaryAmount getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(MonetaryAmount finalPrice) {
        this.finalPrice = finalPrice;
    }
}
