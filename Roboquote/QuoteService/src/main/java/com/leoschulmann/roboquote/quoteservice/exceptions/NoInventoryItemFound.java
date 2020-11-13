package com.leoschulmann.roboquote.quoteservice.exceptions;

public class NoInventoryItemFound extends RuntimeException {
    int id;
    public NoInventoryItemFound(Integer inventoryId) {
        super();
        id = inventoryId;
    }
}
