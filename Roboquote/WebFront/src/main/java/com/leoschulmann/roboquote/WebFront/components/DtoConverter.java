package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.itemservice.dto.ItemDto;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ISO_DATE;

@Service
public class DtoConverter {
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

}
