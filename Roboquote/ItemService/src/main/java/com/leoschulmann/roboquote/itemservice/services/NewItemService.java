package com.leoschulmann.roboquote.itemservice.services;

import com.leoschulmann.roboquote.itemservice.dto.ItemDto;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.itemservice.repositories.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewItemService {

    private final ItemRepository itemRepository;
    private final ItemBundleDtoConverter dtoConverter;

    public List<ItemDto> getItemDtoByIds(List<Integer> ids) {
        List<Item> items = itemRepository.findAllById(ids);
        return items.stream().map(dtoConverter::convertToItemDto).collect(Collectors.toList());
    }

    public ItemDto getItemById(int id) {
        return dtoConverter.convertToItemDto(itemRepository.findById(id).get());
    }

    public List<ItemDto> getAllDtos() {
        return itemRepository.findAll().stream().map(dtoConverter::convertToItemDto).collect(Collectors.toList());
    }

    public Integer addNewItem(ItemDto itemDto) {
        Item converted = dtoConverter.convertToItem(itemDto);
        return itemRepository.save(converted).getId();
    }

    public Integer editItem(int id, ItemDto itemDto) {
        Item persisted = itemRepository.findById(id).get(); //validated in controller
        persisted = dtoConverter.updateFields(persisted, itemDto);
        return itemRepository.save(persisted).getId();
    }

    public void deleteItem(int id) {
        itemRepository.deleteById(id);
    }

    public List<ItemDto> searchByQuery(String query) {
        List<Item> items = itemRepository.
                findAllByNameRusContainingIgnoreCaseOrNameEngContainingIgnoreCaseOrPartnoContainingIgnoreCase
                        (query, query, query);
        return items.stream().map(dtoConverter::convertToItemDto).collect(Collectors.toList());
    }
}
