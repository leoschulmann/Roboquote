package com.leoschulmann.roboquote.itemservice.controller;

import com.leoschulmann.roboquote.itemservice.dto.BundleDto;
import com.leoschulmann.roboquote.itemservice.services.BundleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bundle")
public class BundleController {

    private final BundleService bundleService;

    public BundleController(BundleService bundleService) {
        this.bundleService = bundleService;
    }

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
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    ResponseEntity<List<BundleDto>> removeBundle(@PathVariable int id) {
        bundleService.deleteBundle(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("{id}")
    ResponseEntity<BundleDto> editBundle(@PathVariable int id, @RequestBody BundleDto dto) {
        bundleService.editBundle(id, dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
