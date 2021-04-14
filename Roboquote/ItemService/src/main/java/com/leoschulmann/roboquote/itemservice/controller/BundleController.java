package com.leoschulmann.roboquote.itemservice.controller;

import com.leoschulmann.roboquote.itemservice.dto.BundleDto;
import com.leoschulmann.roboquote.itemservice.dto.BundleItemDto;
import com.leoschulmann.roboquote.itemservice.services.BundleService;
import com.leoschulmann.roboquote.itemservice.validation.ExistingBundle;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/bundle")
@Validated
@RequiredArgsConstructor
public class BundleController {

    private final BundleService bundleService;

    private final Validator validator;

    @GetMapping("{id}")
    ResponseEntity<BundleDto> getBundle(@PathVariable @ExistingBundle int id) {
        BundleDto bun = bundleService.getById(id);
        return new ResponseEntity<>(bun, HttpStatus.OK);
    }

    @GetMapping()
    ResponseEntity<List<BundleDto>> getBundlesNamesAndIds() {
        List<BundleDto> bundles = bundleService.getAllBundlesIdsAndNames();
        return new ResponseEntity<>(bundles, HttpStatus.OK);
    }

    @PostMapping()
    ResponseEntity<Object> addNewBundle(@RequestBody @Valid BundleDto requestDto) {
        validateBundleItems(requestDto.getItems());
        bundleService.addNewBundle(requestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    ResponseEntity<Object> removeBundle(@PathVariable @ExistingBundle int id) {
        bundleService.deleteBundle(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("{id}")
    ResponseEntity<Object> editBundle(@PathVariable @ExistingBundle int id, @RequestBody @Valid BundleDto dto) {
        validateBundleItems(dto.getItems());
        bundleService.editBundle(id, dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void validateBundleItems(@Size(min = 1) List<BundleItemDto> items) {
        Set<ConstraintViolation<BundleItemDto>> errors = new HashSet<>();
        items.forEach(i -> errors.addAll(validator.validate(i)));
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(errors);
        }
    }
}
