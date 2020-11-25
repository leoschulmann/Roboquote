package com.leoschulmann.roboquote.quoteservice.exceptions;

public class CreatingXlsxFileException extends RuntimeException {
    private String pic;

    public String getPic() {
        return pic;
    }

    public CreatingXlsxFileException() {
        super();
    }

    public CreatingXlsxFileException(String pic) {
        super();
        this.pic = pic;
    }
}
