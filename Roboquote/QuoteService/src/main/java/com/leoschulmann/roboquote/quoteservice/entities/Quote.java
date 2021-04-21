package com.leoschulmann.roboquote.quoteservice.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.leoschulmann.roboquote.quoteservice.serializers.MoneyDeserializer;
import com.leoschulmann.roboquote.quoteservice.serializers.MoneySerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.javamoney.moneta.Money;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class Quote {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "serial", nullable = false)
    private String number;

    //legacy table entries only. primary usage - createdTimestamp
    @Column(name = "date_created")
    private LocalDate created;

    @Column(name = "timestamp")
    private LocalDateTime createdTimestamp;

    @Column(name = "date_valid")
    private LocalDate validThru;

    @Column(name = "version", columnDefinition = "int default 1")
    private Integer version;

    @Column(name = "customer", nullable = false)
    private String customer;

    @JsonManagedReference
    @OneToMany(mappedBy = "quote", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
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

    @Column(name = "installation")
    private String installation;

    @Column(name = "vat")
    private Integer vat;

    @Column(name = "euro_rate", columnDefinition = "decimal(19,4)")
    private BigDecimal eurRate;

    @Column(name = "usd_rate", columnDefinition = "decimal(19,4)")
    private BigDecimal usdRate;

    @Column(name = "jpy_rate", columnDefinition = "decimal(19,4)")
    private BigDecimal jpyRate;

    @Column(name = "conversion_rate")
    private BigDecimal conversionRate;

    @Column
    private String comment;

    @Column
    private Boolean cancelled;

    @Columns(columns = {
            @Column(name = "final_price_currency"),
            @Column(name = "final_price_amount")})
    @Type(type = "org.jadira.usertype.moneyandcurrency.moneta.PersistentMoneyAmountAndCurrency")
    @JsonDeserialize(using = MoneyDeserializer.class)
    @JsonSerialize(using = MoneySerializer.class)
    private Money finalPrice;

    //todo make overridable price


    //todo manage all these constructors. it's a mess
    public Quote(String number, LocalDate validThru, String customer, String dealer, String customerInfo, String dealerInfo) {
        this.number = number;
        this.validThru = validThru;
        this.customer = customer;
        this.dealer = dealer;
        this.customerInfo = customerInfo;
        this.dealerInfo = dealerInfo;
        this.sections = new ArrayList<>();
        this.createdTimestamp = LocalDateTime.now();
    }

    public Quote(String number, Integer version, String customer, String customerInfo,
                 String dealer, String dealerInfo, String paymentTerms, String shippingTerms,
                 String warranty, String installation, Integer vat, Integer discount, BigDecimal eurRate,
                 BigDecimal usdRate, BigDecimal jpyRate, BigDecimal conversionRate) {
        this.number = number;
        this.version = version;
        this.customer = customer;
        this.discount = discount;
        this.dealer = dealer;
        this.customerInfo = customerInfo;
        this.dealerInfo = dealerInfo;
        this.paymentTerms = paymentTerms;
        this.shippingTerms = shippingTerms;
        this.warranty = warranty;
        this.installation = installation;
        this.vat = vat;
        this.eurRate = eurRate;
        this.usdRate = usdRate;
        this.jpyRate = jpyRate;
        this.conversionRate = conversionRate;
        this.sections = new ArrayList<>();
        this.createdTimestamp = LocalDateTime.now();
    }

    public Quote(Integer discount, Integer vat, BigDecimal eurRate, BigDecimal usdRate, BigDecimal jpyRate,
                 BigDecimal conversionRate) {
        this.discount = discount;
        this.vat = vat;
        this.eurRate = eurRate;
        this.usdRate = usdRate;
        this.jpyRate = jpyRate;
        this.conversionRate = conversionRate;
        this.sections = new ArrayList<>();
        this.createdTimestamp = LocalDateTime.now();
    }

    public Quote(int id, String number, LocalDateTime created, Integer version, String dealer, String customer,
                 Money finalPrice, String comment, boolean cancelled) {
        this.id = id;
        this.number = number;
        this.createdTimestamp = created;
        this.version = version;
        this.dealer = dealer;
        this.customer = customer;
        this.finalPrice = finalPrice;
        this.comment = comment;
        this.cancelled = cancelled;
    }

    public Quote(int id, String number, LocalDateTime createdTimestamp, LocalDate validThru, Integer version,
                 String customer, List<QuoteSection> sections, Integer discount, String dealer, String customerInfo,
                 String dealerInfo, String paymentTerms, String shippingTerms, String warranty, String installation,
                 Integer vat, BigDecimal eurRate, BigDecimal usdRate, BigDecimal jpyRate, BigDecimal conversionRate,
                 Money finalPrice, String comment, Boolean cancelled) {
        this.id = id;
        this.number = number;
        this.createdTimestamp = createdTimestamp;
        this.validThru = validThru;
        this.version = version;
        this.customer = customer;
        this.sections = sections;
        this.discount = discount;
        this.dealer = dealer;
        this.customerInfo = customerInfo;
        this.dealerInfo = dealerInfo;
        this.paymentTerms = paymentTerms;
        this.shippingTerms = shippingTerms;
        this.warranty = warranty;
        this.installation = installation;
        this.vat = vat;
        this.eurRate = eurRate;
        this.usdRate = usdRate;
        this.jpyRate = jpyRate;
        this.conversionRate = conversionRate;
        this.finalPrice = finalPrice;
        this.comment = comment;
        this.cancelled = cancelled;
    }

    public void addSections(QuoteSection... sec) {
        Arrays.stream(sec).forEach(quoteSection -> {
            sections.add(quoteSection);
            quoteSection.setQuote(this);
        });
    }

    public void removeSection(QuoteSection quoteSection) {
        sections.remove(quoteSection);
        quoteSection.setQuote(null);
    }
}
