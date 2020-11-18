package com.leoschulmann.roboquote.quoteservice.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
public class Quote {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "serial", nullable = false)
    private String number;

    @Column(name = "date_created", nullable = false)
    private LocalDate created;

    @Column(name = "date_valid")
    private LocalDate validThru;

    @Column(name = "version", columnDefinition = "int default 1")
    private Integer version;

    @Column(name = "customer", nullable = false)
    private String customer;

    @JsonManagedReference
    @OneToMany(mappedBy = "quote", cascade = {CascadeType.ALL})
    private List<QuoteSection> sections;

    @Column(name = "discount", columnDefinition = "decimal(3,1) default 0.0")
    private Integer discount;

    @Column(name = "dealer")
    private String dealer;

    @Column(name = "customer_info")
    private String customerInfo;

    @Column(name = "dealer_info")
    private String dealerInfo;

    @Column(name = "payment_terms")
    private String paymentTerms;

    @Column(name = "shipping_terms")
    private String shippingTerms;

    @Column(name = "warranty")
    private String warranty;


    //todo make overridable price

    public Quote() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public LocalDate getValidThru() {
        return validThru;
    }

    public void setValidThru(LocalDate validThru) {
        this.validThru = validThru;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public List<QuoteSection> getSections() {
        return sections;
    }

    public void setSections(List<QuoteSection> sections) {
        this.sections = sections;
    }


    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getDealer() {
        return dealer;
    }

    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    public String getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(String customerInfo) {
        this.customerInfo = customerInfo;
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

    public Quote(String number, LocalDate validThru, String customer, String dealer, String customerInfo, String dealerInfo) {
        this.number = number;
        this.validThru = validThru;
        this.customer = customer;
        this.dealer = dealer;
        this.customerInfo = customerInfo;
        this.dealerInfo = dealerInfo;
        this.sections = new ArrayList<>();
        this.created = LocalDate.now();
    }

    public void addSections(QuoteSection... sec) {
        Arrays.stream(sec).forEach(quoteSection -> {
            sections.add(quoteSection);
            quoteSection.setQuote(this);
        });
    }
}
