package com.leoschulmann.roboquote.quoteservice.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.leoschulmann.roboquote.quoteservice.serializers.ItemPositionDeserializer;
import com.leoschulmann.roboquote.quoteservice.serializers.ItemPositionSerializer;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.javamoney.moneta.Money;

import javax.persistence.*;

@Entity
@JsonSerialize(using = ItemPositionSerializer.class)
@JsonDeserialize(using = ItemPositionDeserializer.class)
@Table(name = "item_position")
public class ItemPosition {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "name_in_quote", nullable = false)
    private String name;

    @Columns(columns = {
            @Column(name = "selling_currency", nullable = false),
            @Column(name = "selling_amount", nullable = false)})
    @Type(type = "org.jadira.usertype.moneyandcurrency.moneta.PersistentMoneyAmountAndCurrency")
    private Money sellingPrice;

    @Column(name = "qty", nullable = false)
    private Integer qty;

    @JoinColumn(name = "q_ref")
    @ManyToOne
    private Quote quote;

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

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
}
