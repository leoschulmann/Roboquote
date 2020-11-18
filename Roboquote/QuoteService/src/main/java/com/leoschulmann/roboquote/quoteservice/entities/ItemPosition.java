package com.leoschulmann.roboquote.quoteservice.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.leoschulmann.roboquote.quoteservice.serializers.MoneyDeserializer;
import com.leoschulmann.roboquote.quoteservice.serializers.MoneySerializer;
import com.sun.istack.NotNull;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.javamoney.moneta.Money;

import javax.persistence.*;

@Entity
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

    @Column(name = "inventory_item_id")  //todo make not null
    private Integer itemId;

    public ItemPosition() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Money getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Money sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public QuoteSection getSection() {
        return section;
    }

    public void setSection(QuoteSection quote) {
        this.section = quote;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    public Money getSellingSum() {
        return sellingSum;
    }

    public void setSellingSum(Money sellingSum) {
        this.sellingSum = sellingSum;
    }

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
