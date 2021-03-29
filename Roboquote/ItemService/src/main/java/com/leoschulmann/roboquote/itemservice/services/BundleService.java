package com.leoschulmann.roboquote.itemservice.services;

import com.leoschulmann.roboquote.itemservice.dto.BundleDto;
import com.leoschulmann.roboquote.itemservice.entities.Bundle;
import com.leoschulmann.roboquote.itemservice.entities.projections.BundleWithoutPositions;
import com.leoschulmann.roboquote.itemservice.repositories.BundleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BundleService {

    private final BundleRepository bundleRepository;
    private final DtoConverter dtoConverter;

    public BundleDto getById(int id) {
        Bundle bundle = bundleRepository.findById(id).get(); //validated in controller
        return dtoConverter.convertFromBundle(bundle);
    }

    public List<BundleDto> getAllBundlesIdsAndNames() {
        List<BundleWithoutPositions> projections = bundleRepository.getAllBundlesNamesAndIds();
        return dtoConverter.convertFromProjections(projections);
    }

    public Integer addNewBundle(BundleDto requestDto) {
        Bundle bundle = dtoConverter.convertToBundle(requestDto);
        return bundleRepository.save(bundle).getId();
    }

    public void deleteBundle(int id) {
        bundleRepository.deleteById(id);
    }

    public void editBundle(int id, BundleDto dto) {
        Bundle bundle = bundleRepository.findById(id).get(); //validated in controller
        bundle.setNameRus(dto.getName()); //todo i8n violation
        bundle.setPositions(new ArrayList<>());
        dto.getItems().stream().map(dtoConverter::convertFromBundleItemDto).forEach(bundle::addPosition);
        bundleRepository.save(bundle);
    }
}
