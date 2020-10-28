package com.leoschulmann.roboquote.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.leoschulmann.roboquote.serializers.ItemSerializer;
import org.javamoney.moneta.Money;

@JsonSerialize(using = ItemSerializer.class)
public class Item {
    String partno;
    String nameRus;
    String nameEng;
    Money buyingPrice;
    double margin;
    Money sellingPrice;

    public Item() {
    }

    public Item(String partno, String nameRus, String nameEng, Money buyingPrice, double margin) {
        this.partno = partno;
        this.nameRus = nameRus;
        this.nameEng = nameEng;
        this.buyingPrice = buyingPrice;
        this.margin = margin;
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
        return sellingPrice == null ? buyingPrice.divide((100 - margin) / 100.) : sellingPrice;
    }

    public void setSellingPrice(Money sellingPrice) { //overrides calculation of selling price
        this.sellingPrice = sellingPrice;
    }
}
