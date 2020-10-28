package com.leoschulmann.roboquote.controller;

import com.leoschulmann.roboquote.model.Item;
import org.javamoney.moneta.Money;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class SimpleController{

    @GetMapping("/item")
    public Item getSimpleItem() {
        return new Item("0123",
                "Название",
                "Name", Money.of(1000, "EUR"),
                30);
    }
}
