package com.leoschulmann.roboquote.itemservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

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

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
        String str = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        return new ResponseEntity<>(new ExceptionMessage(str, 400), HttpStatus.BAD_REQUEST);
    }
}
