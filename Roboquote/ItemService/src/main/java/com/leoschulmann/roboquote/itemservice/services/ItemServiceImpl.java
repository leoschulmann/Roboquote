package com.leoschulmann.roboquote.itemservice.services;

import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.itemservice.exceptions.ItemNotFoundException;
import com.leoschulmann.roboquote.itemservice.exceptions.MangledItemException;
import com.leoschulmann.roboquote.itemservice.repositories.ItemRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Deprecated
public class ItemServiceImpl implements ItemService {

    final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Item getItemById(int id) {
        return itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
    }

    @Override
    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    @Override
    public List<Item> searchBy(String str) {
        return itemRepository.
                findAllByNameRusContainingIgnoreCaseOrNameEngContainingIgnoreCaseOrPartnoContainingIgnoreCase
                        (str, str, str);
    }

    @Override
    public void deleteItemById(int id) {
        try {
            itemRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new ItemNotFoundException(id);
        }
    }

    @Override
    public Item saveItem(Item item) {
        try {
            return itemRepository.saveAndFlush(item);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            throw new MangledItemException();
        }
    }
}
