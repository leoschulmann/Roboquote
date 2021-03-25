package com.leoschulmann.roboquote.itemservice.entities;

import javax.persistence.*;

@Entity
@Table(name = "bundled_pos")
public class BundledPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "qty")
    private Integer qty;

    @ManyToOne
    @JoinColumn(name = "bundle_id")
    private Bundle parentBundle;

    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;

    public int getId() {
        return id;
    }

    public BundledPosition() {
    }

    public BundledPosition(Integer qty, Item item) {
        this.qty = qty;
        this.item = item;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Bundle getBundle() {
        return parentBundle;
    }

    public void setBundle(Bundle bundle) {
        this.parentBundle = bundle;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
