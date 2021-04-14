package com.leoschulmann.roboquote.itemservice.services;

import com.leoschulmann.roboquote.itemservice.dto.BundleDto;
import com.leoschulmann.roboquote.itemservice.dto.BundleItemDto;
import com.leoschulmann.roboquote.itemservice.dto.ItemDto;
import com.leoschulmann.roboquote.itemservice.entities.Bundle;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.itemservice.entities.projections.BundleWithoutPositions;
import lombok.RequiredArgsConstructor;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_DATE;

@Service
@RequiredArgsConstructor
public class ItemBundleDtoConverter {

    public BundleDto convertFromBundle(Bundle bundle) {
        BundleDto dto = new BundleDto(bundle.getId(), bundle.getNameRus(), new ArrayList<>()); //todo i8n violation

        bundle.getPositions().forEach(pos -> dto.getItems()
                .add(new BundleItemDto(pos.getItem().getId(), pos.getQty(), pos.getItem().getNameRus())));
        return dto;
    }

    public List<BundleDto> convertFromProjections(List<BundleWithoutPositions> projections) { //todo i8n violation
        return projections.stream().map(p -> new BundleDto(p.getId(), p.getNameRus())).collect(Collectors.toList());
    }

    public ItemDto convertToItemDto(Item i) {
        return new ItemDto(i.getId(), i.getPartno(), i.getBrand(), i.getNameRus(), i.getNameEng(), i.getMargin(),
                i.getBuyingPrice().getCurrency().getCurrencyCode(), i.getBuyingPrice().getNumberStripped().doubleValue(),
                i.getSellingPrice().getCurrency().getCurrencyCode(), i.getSellingPrice().getNumberStripped().doubleValue(),
                i.getCreated().format(ISO_DATE), i.getModified().format(ISO_DATE),
                i.isOverridden());
    }


    public Item convertToItem(ItemDto d) {
        String buyingCur = d.getCurrencyBuying();
        BigDecimal buyingAmt = BigDecimal.valueOf(d.getAmountBuying());
        String sellingCur = d.getCurrencySelling();
        BigDecimal sellingAmt = BigDecimal.valueOf(d.getAmountSelling());

        return new Item(d.getId(), d.getBrand(), d.getPartNumber(), d.getNameRus(), d.getNameEng(),
                Money.of(buyingAmt, buyingCur), d.getSellingMargin(), Money.of(sellingAmt, sellingCur),
                LocalDate.parse(d.getDateCreated(), ISO_DATE), LocalDate.parse(d.getDateModified(), ISO_DATE),
                d.getOverriddenSellPrice());
    }

    public Item updateFields(Item persisted, ItemDto d) {
        String buyingCur = d.getCurrencyBuying();
        BigDecimal buyingAmt = BigDecimal.valueOf(d.getAmountBuying());
        String sellingCur = d.getCurrencySelling();
        BigDecimal sellingAmt = BigDecimal.valueOf(d.getAmountSelling());

        persisted.setBrand(d.getBrand());
        persisted.setPartno(d.getPartNumber());
        persisted.setNameRus(d.getNameRus());
        persisted.setNameEng(d.getNameEng());
        persisted.setBuyingPrice(Money.of(buyingAmt, buyingCur));
        persisted.setMargin(d.getSellingMargin());
        persisted.setSellingPrice(Money.of(sellingAmt, sellingCur));
        persisted.setModified(LocalDate.parse(d.getDateModified(), ISO_DATE));
        persisted.setOverridden(d.getOverriddenSellPrice());
        return persisted;
    }
}

