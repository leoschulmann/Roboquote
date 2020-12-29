package com.leoschulmann.roboquote.itemservice.exceptions;

public class ItemNotFoundException extends RuntimeException{
    int id;

    public ItemNotFoundException(int id) {
        super();
        this.id = id;
    }
}
