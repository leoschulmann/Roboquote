package com.leoschulmann.roboquote.quoteservice.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Quote {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    @Column(name = "serial")
    String number;

    @Column(name = "date_created")
    LocalDate created;

    @Column(name = "date_valid")
    LocalDate validThru;

    @Column(name = "version")
    Integer version;

    @Column(name = "customer")
    String customer;

    @OneToMany(mappedBy = "quote", cascade = {CascadeType.ALL})
    List<ItemPosition> itemPositions;


    public Quote() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public LocalDate getValidThru() {
        return validThru;
    }

    public void setValidThru(LocalDate validThru) {
        this.validThru = validThru;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public List<ItemPosition> getItemPositions() {
        return itemPositions;
    }

    public void setItemPositions(List<ItemPosition> itemPositions) {
        this.itemPositions = itemPositions;
    }
}
