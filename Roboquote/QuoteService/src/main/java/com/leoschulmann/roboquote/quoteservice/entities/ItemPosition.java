package com.leoschulmann.roboquote.quoteservice.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.leoschulmann.roboquote.quoteservice.serializers.MoneyDeserializer;
import com.leoschulmann.roboquote.quoteservice.serializers.MoneySerializer;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.javamoney.moneta.Money;

import javax.persistence.*;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "item_position")
public class ItemPosition {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "name_in_quote", nullable = false)
    private String name;

    @Column(name = "part_no")
    private String partNo;

    @Columns(columns = {
            @Column(name = "selling_currency", nullable = false),
            @Column(name = "selling_amount", nullable = false)})
    @Type(type = "org.jadira.usertype.moneyandcurrency.moneta.PersistentMoneyAmountAndCurrency")
    @JsonDeserialize(using = MoneyDeserializer.class)
    @JsonSerialize(using = MoneySerializer.class)
    private Money sellingPrice;

    @Column(name = "qty", nullable = false)
    private Integer qty;

    @Columns(columns = {
            @Column(name = "selling_sum_currency", nullable = false),
            @Column(name = "selling_sum_amount", nullable = false)})
    @Type(type = "org.jadira.usertype.moneyandcurrency.moneta.PersistentMoneyAmountAndCurrency")
    @JsonDeserialize(using = MoneyDeserializer.class)
    @JsonSerialize(using = MoneySerializer.class)
    private Money sellingSum;

    @JoinColumn(name = "section_ref", nullable = false)
    @ManyToOne
    @JsonBackReference
    private QuoteSection section;

    @Column(name = "inventory_item_id")
    private Integer itemId;

    public ItemPosition(String name, String partNo, @NotNull Money sellingPrice, @NotNull Integer qty, Integer itemId) {
        this.name = name;
        this.partNo = partNo;
        this.sellingPrice = sellingPrice;
        this.qty = qty;
        this.itemId = itemId;
        this.sellingSum = sellingPrice.multiply(qty);
    }

    public void incrementQty() {
        this.qty++;
        this.sellingSum = sellingPrice.multiply(qty);
    }
}
