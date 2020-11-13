package com.leoschulmann.roboquote.quoteservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Component
@ControllerAdvice
public class ExceptionProcessor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EmptyQuoteException.class)
    public ResponseEntity<Object> handleEmptyQuoteExc(EmptyQuoteException e) {
        return new ResponseEntity<>(new ExceptionMessage("Empty quote", 400), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoInventoryItemFound.class)
    public ResponseEntity<Object> noInventoryItemFoundForId(NoInventoryItemFound e) {
        return new ResponseEntity<>(new ExceptionMessage("No item for id " + e.id + " found", 404), HttpStatus.NOT_FOUND);
    }
}
