package com.leoschulmann.roboquote.itemservice.services;

import com.leoschulmann.roboquote.itemservice.dto.BundleDto;
import com.leoschulmann.roboquote.itemservice.dto.PostitionDto;
import com.leoschulmann.roboquote.itemservice.entities.Bundle;
import com.leoschulmann.roboquote.itemservice.entities.BundledPosition;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.itemservice.repositories.BundleRepository;
import com.leoschulmann.roboquote.itemservice.repositories.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BundleService {

    private final BundleRepository bundleRepository;

    private final ItemRepository itemRepository;

    public BundleService(BundleRepository bundleRepository, ItemRepository itemRepository) {
        this.bundleRepository = bundleRepository;
        this.itemRepository = itemRepository;
    }

    public BundleDto getById(int id) {
        Bundle bundle = bundleRepository.findById(id).orElseThrow(() -> new RuntimeException("no bundle for id=" + id));
        BundleDto dto = new BundleDto(bundle.getId(), bundle.getNameRus(), new ArrayList<>());
        for (BundledPosition pos : bundle.getPositions()) {
            dto.getItems().add(new PostitionDto(pos.getItem().getId(), pos.getQty(), pos.getItem().getNameRus()));
        }
        return dto;
    }

    public List<BundleDto> getAll() {
        List<Bundle> listDomain = bundleRepository.findAll();
        return listDomain.stream().map(dom -> new BundleDto(dom.getId(), dom.getNameRus(), null)).collect(Collectors.toList());
    }

    public BundleDto addNewBundle(BundleDto requestDto) {
        Bundle b = new Bundle();
        b.setNameRus(requestDto.getName());
        addItemsFromDto(b, requestDto);
        Bundle persisted = bundleRepository.save(b);
        return getById(persisted.getId());
    }

    public void deleteBundle(int id) {
        bundleRepository.deleteById(id);
    }

    public void editBundle(int id, BundleDto dto) {
        Bundle bundle = bundleRepository.findById(id).orElseThrow(() -> new RuntimeException("no bundle for id=" + id));
        bundle.setNameRus(dto.getName());
        bundle.setPositions(new ArrayList<>());
        addItemsFromDto(bundle, dto);
        bundleRepository.save(bundle);
    }

    private void addItemsFromDto(Bundle bundle, BundleDto bundleDto) {
        for (PostitionDto posDto : bundleDto.getItems()) {
            BundledPosition pos = new BundledPosition();
            pos.setQty(posDto.getQty());
            Item i = itemRepository.findById(posDto.getId())
                    .orElseThrow(() -> new RuntimeException("no item for id=" + posDto.getId()));
            pos.setItem(i);
            bundle.addPosition(pos);
        }
    }
}
