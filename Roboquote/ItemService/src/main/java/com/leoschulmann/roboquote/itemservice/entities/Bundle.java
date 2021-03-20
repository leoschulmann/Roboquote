package com.leoschulmann.roboquote.itemservice.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
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
        pos.setBundle(this);
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

    public List<BundledPosition> getPositions() {
        return positions;
    }

    public void setPositions(List<BundledPosition> positions) {
        this.positions = positions;
    }

    public int getId() {
        return id;
    }

    public void removePosition(BundledPosition bp) {
        positions.remove(bp);
    }
}
