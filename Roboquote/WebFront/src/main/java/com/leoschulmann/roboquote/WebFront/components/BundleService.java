package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.itemservice.dto.BundleDto;
import com.leoschulmann.roboquote.itemservice.entities.Bundle;
import com.leoschulmann.roboquote.itemservice.entities.BundledPosition;
import com.leoschulmann.roboquote.itemservice.entities.Item;

import java.util.List;

public interface BundleService {
    List<BundleDto> getBundlesList();

    BundleDto getBundleDtoById(int id);

    BundledPosition convertToBundlePosition(Item item);

    void saveBundle(Bundle bundle);

    Bundle convertToBundle(BundleDto dto);

    void updateBundle(Bundle bundle);

    void deleteBundle(int id);

    Bundle getBundleById(int bunId);
}
