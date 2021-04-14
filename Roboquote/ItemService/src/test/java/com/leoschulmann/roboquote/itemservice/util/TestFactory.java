package com.leoschulmann.roboquote.itemservice.util;

import com.leoschulmann.roboquote.itemservice.entities.Bundle;
import com.leoschulmann.roboquote.itemservice.entities.BundledPosition;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import org.javamoney.moneta.Money;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TestFactory {
    private static Random random = new Random();

    public static List<Item> itemFactory(String... names) {
        List<Item> list = new ArrayList<>();
        for (String name : names) {
            Item item = new Item();
            item.setNameRus(name);
            item.setNameEng(name);
            item.setModified(LocalDate.now());
            item.setCreated(LocalDate.now());
            item.setPartno(String.valueOf(random.nextInt(1000000)));
            item.setMargin(random.nextInt(99));
            item.setBrand(String.valueOf(random.nextInt(100000)));
            item.setBuyingPrice(Money.of(random.nextInt(10000), "USD"));
            item.setSellingPrice(Money.of(random.nextInt(10000), "USD"));
            item.setOverridden(random.nextBoolean());
            list.add(item);
        }
        return list;
    }

    public static Bundle bundleFactory(String name, Item... items) {
        Bundle bundle = new Bundle();
        bundle.setNameEng(name);
        bundle.setNameRus(name);
        Arrays.stream(items).map(item -> new BundledPosition(random.nextInt(20), item))
                .forEach(bundle::addPosition);
        return bundle;
    }
}
