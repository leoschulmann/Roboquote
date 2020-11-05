package com.leoschulmann.roboquote.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.leoschulmann.roboquote.serializers.ItemDeserializer;
import com.leoschulmann.roboquote.serializers.ItemSerializer;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.javamoney.moneta.Money;
import org.springframework.format.annotation.DateTimeFormat;

import javax.money.Monetary;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@JsonSerialize(using = ItemSerializer.class)
@JsonDeserialize(using = ItemDeserializer.class)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    int id;

    @Column(name = "brand")
    String brand;

    @Column(name = "part_no", nullable = false)
    String partno;

    @Column(name = "name_rus", nullable = false)
    String nameRus;

    @Column(name = "name_eng")
    String nameEng;

    @Columns(columns = {
            @Column(name = "buying_currency"),
            @Column(name = "buying_amount")})
    @Type(type = "org.jadira.usertype.moneyandcurrency.moneta.PersistentMoneyAmountAndCurrency")
    Money buyingPrice;

    @Column(name = "selling_margin")
    double margin;

    @Columns(columns = {
            @Column(name = "selling_currency"),
            @Column(name = "selling_amount")})
    @Type(type = "org.jadira.usertype.moneyandcurrency.moneta.PersistentMoneyAmountAndCurrency")
    Money sellingPrice;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate created;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate modified;

    public Item() {
    }

    public int getId() {
        return id;
    }

    public String getPartno() {
        return partno;
    }

    public void setPartno(String partno) {
        this.partno = partno;
    }

    public String getNameRus() {
        return nameRus;
    }

    public void setNameRus(String nameRus) {
        this.nameRus = nameRus;
    }

    public String getNameEng() {
        return nameEng;
    }

    public void setNameEng(String nameEng) {
        this.nameEng = nameEng;
    }

    public Money getBuyingPrice() {
        return buyingPrice;
    }

    public void setBuyingPrice(Money buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public double getMargin() {
        return margin;
    }

    public void setMargin(double margin) {
        this.margin = margin;
    }

    public Money getSellingPrice() {
        if (sellingPrice.isNegativeOrZero()) sellingPrice = buyingPrice.divide((100 - margin) / 100.);
        return sellingPrice.with(Monetary.getDefaultRounding());
    }

    public void setSellingPrice(Money sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public LocalDate getModified() {
        return modified;
    }

    public void setModified(LocalDate modified) {
        this.modified = modified;
    }
}
