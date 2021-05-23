package com.leoschulmann.roboquote.quoteservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class XlsxDataObject {
    private String fileName;
    private byte[] data;
}
