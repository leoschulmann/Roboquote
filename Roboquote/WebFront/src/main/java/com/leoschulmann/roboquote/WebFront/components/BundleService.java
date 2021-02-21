package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.itemservice.dto.BundleDto;

import java.util.List;

public interface BundleService {
    List<BundleDto> getBundlesList();

    BundleDto getBundleById(int id);
}
