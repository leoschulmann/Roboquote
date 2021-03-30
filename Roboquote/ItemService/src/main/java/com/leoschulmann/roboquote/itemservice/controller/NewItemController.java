package com.leoschulmann.roboquote.itemservice.controller;

import com.leoschulmann.roboquote.itemservice.dto.ItemDto;
import com.leoschulmann.roboquote.itemservice.services.NewItemService;
import com.leoschulmann.roboquote.itemservice.validation.ExistingItem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/item/new") //todo remove stub
@RequiredArgsConstructor
@Validated
public class NewItemController {

    private final NewItemService newItemService;

    @PostMapping("/multiple")
    ResponseEntity<List<ItemDto>> getMultiple(@RequestBody List<Integer> ids) {
        List<ItemDto> list = newItemService.getItemDtoByIds(ids);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<ItemDto> getSingle(@PathVariable @ExistingItem int id) {
        return new ResponseEntity<>(newItemService.getItemById(id), HttpStatus.OK);
    }
}
