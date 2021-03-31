package com.leoschulmann.roboquote.itemservice.dto;

import com.leoschulmann.roboquote.itemservice.validation.ExistingItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemId {
    @ExistingItem
    Integer id;
}
