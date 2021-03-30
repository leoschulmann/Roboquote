package com.leoschulmann.roboquote.itemservice.repositories;

import com.leoschulmann.roboquote.itemservice.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Item findByNameRus(String name);
    List<Item> findAllByNameRusContainingIgnoreCaseOrNameEngContainingIgnoreCaseOrPartnoContainingIgnoreCase
            (String s1, String s2, String s3);


    List<Item> findByIdIn(List<Integer> ids);
}
