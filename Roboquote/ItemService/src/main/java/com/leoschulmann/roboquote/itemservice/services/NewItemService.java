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
    private final DtoConverter dtoConverter;

    public List<ItemDto> getItemDtoByIds(List<Integer> ids) {
        List<Item> items = itemRepository.findAllById(ids);
        return items.stream().map(dtoConverter::convertToItemDto).collect(Collectors.toList());
    }

    public ItemDto getItemById(int id) {
        return dtoConverter.convertToItemDto(itemRepository.findById(id).get());
    }
}
