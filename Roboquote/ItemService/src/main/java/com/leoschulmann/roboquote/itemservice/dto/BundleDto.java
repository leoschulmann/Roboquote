package com.leoschulmann.roboquote.itemservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class BundleDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int id;

    @Size(min = 1, max = 100)
    private String name;

    @Size(min = 1)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<BundleItemDto> items;

    public BundleDto(String name, List<BundleItemDto> items) {
        this.name = name;
        this.items = items;
    }

    public BundleDto(int id, @NotBlank String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
