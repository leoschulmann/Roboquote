package com.leoschulmann.roboquote.itemservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Component
@ControllerAdvice
public class ExceptionProcessor extends ResponseEntityExceptionHandler {
    @ExceptionHandler(MangledItemException.class)
    public ResponseEntity<Object> handleMangledItemException(MangledItemException exception) {
        return new ResponseEntity<>(new ExceptionMessage("Malformed item object!", 400),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<Object> handleItem404(ItemNotFoundException e) {
        return new ResponseEntity<>(new ExceptionMessage("Item id=" + e.id + " not found!", 404),
                HttpStatus.NOT_FOUND);
    }
}
