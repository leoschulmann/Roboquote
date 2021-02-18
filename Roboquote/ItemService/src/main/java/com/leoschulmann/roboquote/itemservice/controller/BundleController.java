package com.leoschulmann.roboquote.itemservice.controller;

import com.leoschulmann.roboquote.itemservice.dto.BundleDto;
import com.leoschulmann.roboquote.itemservice.services.BundleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bundle")
public class BundleController {

    @Autowired
    BundleService bundleService;

    @GetMapping("{id}")
    ResponseEntity<BundleDto> getBundle(@PathVariable int id) {
        BundleDto bun = bundleService.getById(id);
        return new ResponseEntity<>(bun, HttpStatus.OK);
    }

    @GetMapping()
    ResponseEntity<List<BundleDto>> getBundles() {
        List<BundleDto> bundles = bundleService.getAll();
        return new ResponseEntity<>(bundles, HttpStatus.OK);
    }

    @PostMapping()
    ResponseEntity<BundleDto> addNewBundle(@RequestBody BundleDto requestDto) {
        BundleDto responseDto = bundleService.addNewBundle(requestDto);
        return new ResponseEntity<>(requestDto, HttpStatus.OK);
    }
}
