package com.leoschulmann.roboquote.itemservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class BundleDto {
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<PostitionDto> items;

    public BundleDto() {
    }

    public BundleDto(String name, List<PostitionDto> items) {
        this.name = name;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PostitionDto> getItems() {
        return items;
    }

    public void setItems(List<PostitionDto> items) {
        this.items = items;
    }
}
