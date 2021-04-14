package com.leoschulmann.roboquote.itemservice.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "bundle")
public class Bundle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "name_rus")
    private String nameRus;

    @Column(name = "name_eng")
    private String nameEng;

    @OneToMany(mappedBy = "parentBundle", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BundledPosition> positions;

    public Bundle() {
        positions = new ArrayList<>();
    }

    public void addPosition(BundledPosition pos) {
        positions.add(pos);
        pos.setParentBundle(this);
    }

    public void removePosition(BundledPosition bp) {
        positions.remove(bp);
    }

    @Override
    public String toString() {
        return nameRus;
    }
}
