package com.leoschulmann.roboquote.quoteservice.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryFunctions;


import javax.money.MonetaryAmount;
import javax.persistence.*;
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


    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ItemPosition> positions;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "discount", columnDefinition = "decimal(3,1) default 0.0")
    private Integer discount;

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

    public void setPositions(List<ItemPosition> positions) {
        this.positions = positions;
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

    public MonetaryAmount getTotal() {
        //todo do smth with mismatching currencies within one table
        return getPositions().stream()
                .map(ip -> (MonetaryAmount) (ip.getSellingSum()))
                .reduce(MonetaryFunctions.sum())
                .orElseGet(() -> Money.of(0, "EUR"));
    }

    public MonetaryAmount getTotalDiscounted() {
        return getTotal().multiply((100 - discount) / 100.);
    }
}
