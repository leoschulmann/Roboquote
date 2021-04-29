package com.leoschulmann.roboquote.quoteservice.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

import javax.money.MonetaryAmount;
import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
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

    @Column(name = "discount", columnDefinition = "decimal(10,8) default 0.0")
    private BigDecimal discount;

    @Columns(columns = {
            @Column(name = "subtotal_currency"),
            @Column(name = "subtotal_amount")})
    @Type(type = "org.jadira.usertype.moneyandcurrency.moneta.PersistentMoneyAmountAndCurrency")
    @JsonDeserialize(using = MoneyDeserializer.class)
    @JsonSerialize(using = MoneySerializer.class)
    private Money total = Money.of(BigDecimal.ZERO, "EUR");  //todo maybe it should be transient

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "q_ref", nullable = false)
    private Quote quote;

    public QuoteSection(String name) {
        this.name = name;
        positions = new ArrayList<>();
        discount = BigDecimal.ZERO;
    }

    public void addItemPositions(ItemPosition... pos) {
        Arrays.stream(pos).forEach(itemPosition -> {
            itemPosition.setSection(this);
            positions.add(itemPosition);
        });
    }

    public QuoteSection(List<ItemPosition> positions, String name, BigDecimal discount, Money total) {
        this.positions = positions;
        this.name = name;
        this.discount = discount;
        this.total = total;
    }

    @JsonIgnore
    public MonetaryAmount getTotalDiscounted() {
        return getTotal().multiply((BigDecimal.valueOf(100).subtract(discount).divide(BigDecimal.valueOf(100),
                8, RoundingMode.HALF_UP)));
    }
}
