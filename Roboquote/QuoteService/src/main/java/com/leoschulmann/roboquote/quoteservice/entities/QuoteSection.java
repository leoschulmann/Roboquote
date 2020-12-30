package com.leoschulmann.roboquote.quoteservice.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.leoschulmann.roboquote.quoteservice.serializers.MoneyDeserializer;
import com.leoschulmann.roboquote.quoteservice.serializers.MoneySerializer;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "quote_section")
public class QuoteSection {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<ItemPosition> positions;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "discount", columnDefinition = "decimal(3,1) default 0.0")
    private Integer discount;

    @Columns(columns = {
            @Column(name = "subtotal_currency"),
            @Column(name = "subtotal_amount")})
    @Type(type = "org.jadira.usertype.moneyandcurrency.moneta.PersistentMoneyAmountAndCurrency")
    @JsonDeserialize(using = MoneyDeserializer.class)
    @JsonSerialize(using = MoneySerializer.class)
    private Money total = Money.of(BigDecimal.ZERO, "EUR");

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "q_ref", nullable = false)
    private Quote quote;

    public QuoteSection() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ItemPosition> getPositions() {
        return positions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public Money getTotal() {
        return total;
    }

    public void setTotal(MonetaryAmount total) {
        this.total = (Money) total;
    }

    public QuoteSection(String name) {
        this.name = name;
        positions = new ArrayList<>();
        discount = 0;
    }

    public void addItemPositions(ItemPosition... pos) {
        Arrays.stream(pos).forEach(itemPosition -> {
            itemPosition.setSection(this);
            positions.add(itemPosition);
        });
    }

    @JsonIgnore
    public MonetaryAmount getTotalDiscounted() {
        return getTotal().multiply((100 - discount) / 100.);
    }
}
