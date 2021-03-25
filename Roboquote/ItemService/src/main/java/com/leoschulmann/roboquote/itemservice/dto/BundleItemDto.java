package com.leoschulmann.roboquote.itemservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public class BundleItemDto {
    private int id;
    private int qty;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BundleItemDto() {
    }

    public BundleItemDto(int id, int qty) {
        this.id = id;
        this.qty = qty;
    }

    public BundleItemDto(int id, int qty, String name) {
        this.id = id;
        this.qty = qty;
        this.name = name;
    }
}
