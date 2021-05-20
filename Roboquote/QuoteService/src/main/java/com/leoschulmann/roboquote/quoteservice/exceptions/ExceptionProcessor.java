package com.leoschulmann.roboquote.quoteservice.exceptions;

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

    @ExceptionHandler(EmptyQuoteException.class)
    public ResponseEntity<Object> handleEmptyQuoteExc(EmptyQuoteException e) {
        return new ResponseEntity<>(new ExceptionMessage("Empty quote", 400), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoInventoryItemFound.class)
    public ResponseEntity<Object> noInventoryItemFoundForId(NoInventoryItemFound e) {
        return new ResponseEntity<>(new ExceptionMessage("No item for id " + e.id + " found", 404), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoQuoteFoundException.class)
    public ResponseEntity<Object> noQuoteFoundForID(NoQuoteFoundException e) {
        return new ResponseEntity<>(new ExceptionMessage("No quote found for id " + e.id, 404), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CreatingXlsxFileException.class)
    public ResponseEntity<Object> errorInExcelController(CreatingXlsxFileException e) {
        String message = (e.getPic() == null) ? "Exception writing file to byte array" :
                "Exception adding picture " + e.getPic() + "file";

        return new ResponseEntity<>(new ExceptionMessage(message, 500), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(" "));
        return new ResponseEntity<>(msg, HttpStatus.NOT_EXTENDED);
    }
}
