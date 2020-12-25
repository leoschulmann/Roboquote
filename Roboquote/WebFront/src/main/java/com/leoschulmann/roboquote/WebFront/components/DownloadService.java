package com.leoschulmann.roboquote.WebFront.components;

public interface DownloadService {
    byte[] downloadXlsx(int id);

    String getExtension();
}
