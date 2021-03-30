package com.leoschulmann.roboquote.itemservice.services;

import com.leoschulmann.roboquote.itemservice.dto.BundleDto;
import com.leoschulmann.roboquote.itemservice.dto.BundleItemDto;
import com.leoschulmann.roboquote.itemservice.dto.ItemDto;
import com.leoschulmann.roboquote.itemservice.entities.Bundle;
import com.leoschulmann.roboquote.itemservice.entities.BundledPosition;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.itemservice.entities.projections.BundleWithoutPositions;
import com.leoschulmann.roboquote.itemservice.repositories.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DtoConverter {

    private final ItemRepository itemRepository;

    public BundleDto convertFromBundle(Bundle bundle) {
        BundleDto dto = new BundleDto(bundle.getId(), bundle.getNameRus(), new ArrayList<>()); //todo i8n violation

        bundle.getPositions().forEach(pos -> dto.getItems()
                .add(new BundleItemDto(pos.getItem().getId(), pos.getQty(), pos.getItem().getNameRus())));
        return dto;
    }

    public List<BundleDto> convertFromProjections(List<BundleWithoutPositions> projections) { //todo i8n violation
        return projections.stream().map(p -> new BundleDto(p.getId(), p.getNameRus())).collect(Collectors.toList());
    }

    public Bundle convertToBundle(BundleDto dto) {
        Bundle bundle = new Bundle();
        bundle.setNameRus(dto.getName()); //todo i8n violation
        dto.getItems().stream().map(this::convertFromBundleItemDto).forEach(bundle::addPosition);
        return bundle;
    }

    public BundledPosition convertFromBundleItemDto(BundleItemDto itemDto) {
        Item item = itemRepository.findById(itemDto.getItemId()).get();
        return new BundledPosition(itemDto.getQty(), item);
    }

    public ItemDto convertToItemDto(Item i) {
        return new ItemDto(i.getId(), i.getPartno(), i.getBrand(), i.getNameRus(), i.getNameEng(), i.getMargin(),
                i.getBuyingPrice().getCurrency().getCurrencyCode(), i.getBuyingPrice().getNumberStripped().doubleValue(),
                i.getSellingPrice().getCurrency().getCurrencyCode(), i.getSellingPrice().getNumberStripped().doubleValue(),
                i.getCreated().format(DateTimeFormatter.ISO_DATE), i.getModified().format(DateTimeFormatter.ISO_DATE),
                i.isOverridden());
    }
}
