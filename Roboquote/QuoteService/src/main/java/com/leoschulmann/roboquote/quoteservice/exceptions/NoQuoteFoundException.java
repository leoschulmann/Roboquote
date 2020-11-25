package com.leoschulmann.roboquote.quoteservice.exceptions;

public class NoQuoteFoundException extends RuntimeException {
    int id;

    public NoQuoteFoundException(Integer id) {
        super();
        this.id = id;
    }
}
