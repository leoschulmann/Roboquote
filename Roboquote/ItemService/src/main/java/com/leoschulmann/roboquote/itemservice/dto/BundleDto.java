package com.leoschulmann.roboquote.itemservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class BundleDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int id;

    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<BundleItemDto> items;

    public BundleDto() {
    }

    public BundleDto(String name, List<BundleItemDto> items) {
        this.name = name;
        this.items = items;
    }

    public BundleDto(int id, String name, List<BundleItemDto> items) {
        this.id = id;
        this.name = name;
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BundleItemDto> getItems() {
        return items;
    }

    public void setItems(List<BundleItemDto> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return name;
    }
}
