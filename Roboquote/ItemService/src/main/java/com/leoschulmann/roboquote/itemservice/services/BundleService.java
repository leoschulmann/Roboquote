package com.leoschulmann.roboquote.itemservice.services;

import com.leoschulmann.roboquote.itemservice.dto.BundleDto;
import com.leoschulmann.roboquote.itemservice.dto.PostitionDto;
import com.leoschulmann.roboquote.itemservice.entities.Bundle;
import com.leoschulmann.roboquote.itemservice.entities.BundledPosition;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.itemservice.repositories.BundleRepository;
import com.leoschulmann.roboquote.itemservice.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BundleService {

    @Autowired
    BundleRepository bundleRepository;

    @Autowired
    ItemRepository itemRepository;

    public BundleDto getById(int id) {
        Bundle bundle = bundleRepository.findById(id).orElseThrow(() -> new RuntimeException("no bundle for id=" + id));
        BundleDto dto = new BundleDto();
        dto.setName(bundle.getNameRus());
        List<PostitionDto> items = new ArrayList<>();
        for (BundledPosition pos : bundle.getPositions()) {
            items.add(new PostitionDto(pos.getItem().getId(), pos.getQty(), pos.getItem().getNameRus()));
        }
        dto.setItems(items);
        return dto;
    }

    public List<BundleDto> getAll() {
        List<Bundle> listDomain = bundleRepository.findAll();
        return listDomain.stream().map(dom -> new BundleDto(dom.getNameRus(), null)).collect(Collectors.toList());
    }

    public BundleDto addNewBundle(BundleDto requestDto) {
        Bundle b = new Bundle();
        b.setNameRus(requestDto.getName());

        for (PostitionDto posDto : requestDto.getItems()) {
            BundledPosition pos = new BundledPosition();
            pos.setQty(posDto.getQty());
            Item i = itemRepository.findById(posDto.getId())
                    .orElseThrow(() -> new RuntimeException("no item for id=" + posDto.getId()));
            pos.setItem(i);
            b.addPosition(pos);
        }
        Bundle persisted = bundleRepository.save(b);
        return getById(persisted.getId());
    }
}
