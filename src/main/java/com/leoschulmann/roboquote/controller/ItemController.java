package com.leoschulmann.roboquote.controller;

import com.leoschulmann.roboquote.entities.Item;
import com.leoschulmann.roboquote.services.ItemServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    ItemServiceImpl itemServiceImpl;

    @GetMapping("/")
    List<Item> findAll() {
        return itemServiceImpl.getAll();
    }

    @GetMapping("/{id}")
    Item findById(@PathVariable int id) {
        return itemServiceImpl.getItemById(id);
    }

    @PostMapping("/")
    void addNewItem(@RequestBody Item item) {
        itemServiceImpl.addNewItem(item);
    }
//    public ItemController(BookRepo bookRepo) {
//        this.bookRepo = bookRepo;
//    }

//    @GetMapping("/item")
//    @Transactional
//    public Book getBook() {
//        Optional<Book> b = bookRepo.findById(1);
//        return b.orElseGet(Book::new);
//    }
//
//    @GetMapping("boook")
//    @Transactional
//    public Book getBoook() {
//        Optional<Book> o = bookRepo.findByName("aaa");
//        return o.orElseGet(Book::new);
//    }


//    @GetMapping("/item")
//    public Item getSimpleItem() {
//        return new Item("0123",
//                "Название",
//                "Name", Money.of(1000, "EUR"),
//                30);
//    }
}
