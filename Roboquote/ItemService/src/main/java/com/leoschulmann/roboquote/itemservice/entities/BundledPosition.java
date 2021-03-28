package com.leoschulmann.roboquote.itemservice.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter @Getter @NoArgsConstructor
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

    public BundledPosition(Integer qty, Item item) {
        this.qty = qty;
        this.item = item;
    }
}
