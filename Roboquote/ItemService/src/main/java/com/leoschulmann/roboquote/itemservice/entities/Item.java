package com.leoschulmann.roboquote.itemservice.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.leoschulmann.roboquote.itemservice.serializers.ItemDeserializer;
import com.leoschulmann.roboquote.itemservice.serializers.ItemSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.javamoney.moneta.Money;
import org.springframework.format.annotation.DateTimeFormat;

import javax.money.Monetary;
import javax.persistence.*;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(name = "overridden_sell_price")
    boolean overridden;

    public Money getSellingPrice() {
        return isOverridden() ? sellingPrice.with(Monetary.getDefaultRounding()) :
                buyingPrice.divide((100 - margin) / 100.).with(Monetary.getDefaultRounding());
    }

    public Item(String brand, String partno, String nameRus, String nameEng, Money buyingPrice, double margin,
                Money sellingPrice, LocalDate created, LocalDate modified, boolean overridden) {
        this.brand = brand;
        this.partno = partno;
        this.nameRus = nameRus;
        this.nameEng = nameEng;
        this.buyingPrice = buyingPrice;
        this.margin = margin;
        this.sellingPrice = sellingPrice;
        this.created = created;
        this.modified = modified;
        this.overridden = overridden;
    }
}
