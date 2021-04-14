package com.leoschulmann.roboquote.itemservice.controller;

import com.leoschulmann.roboquote.itemservice.dto.ItemDto;
import com.leoschulmann.roboquote.itemservice.dto.ItemId;
import com.leoschulmann.roboquote.itemservice.services.ItemService;
import com.leoschulmann.roboquote.itemservice.validation.ExistingItem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService newItemService;

    @PostMapping("/multiple")
    ResponseEntity<List<ItemDto>> getMultiple(@RequestBody @Valid List<ItemId> ids) {
        List<ItemDto> list = newItemService.getItemDtoByIds(ids.stream().map(ItemId::getId).collect(Collectors.toList()));
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<ItemDto> getSingle(@PathVariable @ExistingItem int id) {
        return new ResponseEntity<>(newItemService.getItemById(id), HttpStatus.OK);
    }

    @GetMapping
    ResponseEntity<List<ItemDto>> getAll() {
        return new ResponseEntity<>(newItemService.getAllDtos(), HttpStatus.OK);
    }

    @PostMapping
    ResponseEntity<Object> addNew(@RequestBody @Valid ItemDto itemDto) {
        return new ResponseEntity<>(newItemService.addNewItem(itemDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    ResponseEntity<Object> edit(@RequestBody @Valid ItemDto itemDto, @PathVariable @ExistingItem int id) {
        return new ResponseEntity<>(newItemService.editItem(id, itemDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Object> delete(@PathVariable @ExistingItem int id) {
        newItemService.deleteItem(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search/{query}")
    ResponseEntity<List<ItemDto>> search(@PathVariable @Size(min = 1, max = 99) String query) {
        return new ResponseEntity<>(newItemService.searchByQuery(query), HttpStatus.OK);
    }
}
