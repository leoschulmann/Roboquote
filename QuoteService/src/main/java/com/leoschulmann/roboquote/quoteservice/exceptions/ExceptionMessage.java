package com.leoschulmann.roboquote.quoteservice.exceptions;

public class ExceptionMessage {
    String message;
    int code;

    public ExceptionMessage() {
    }

    public ExceptionMessage(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
