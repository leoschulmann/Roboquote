package com.leoschulmann.roboquote.WebFront.pojo;

import java.time.LocalDate;

public class QuoteDetails {
    private String name;
    private String customer;
    private String dealer;
    private String email;
    private String paymentTerms;
    private String shippingTerms;
    private String warranty;
    private LocalDate validThru;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
