package com.leoschulmann.roboquote.quoteservice.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
public class Quote {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    @Column(name = "serial", nullable = false)
    String number;

    @Column(name = "date_created", nullable = false)
    LocalDate created;

    @Column(name = "date_valid")
    LocalDate validThru;

    @Column(name = "version", columnDefinition = "int default 1")
    Integer version;

    @Column(name = "customer", nullable = false)
    String customer;

    @JsonManagedReference
    @OneToMany(mappedBy = "quote", cascade = {CascadeType.ALL})
    List<QuoteSection> sections;


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

    public List<QuoteSection> getSections() {
        return sections;
    }

    public void setSections(List<QuoteSection> sections) {
        this.sections = sections;
    }

    public Quote(String number, String customer, LocalDate validThru) {
        this.number = number;
        this.created = LocalDate.now();
        this.customer = customer;
        this.sections = new ArrayList<>();
        this.validThru = validThru;
    }

    public void addSections(QuoteSection... sec) {
        Arrays.stream(sec).forEach(quoteSection -> {
            sections.add(quoteSection);
            quoteSection.setQuote(this);
        });
    }
}
