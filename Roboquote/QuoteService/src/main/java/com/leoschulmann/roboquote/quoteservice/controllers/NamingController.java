package com.leoschulmann.roboquote.quoteservice.controllers;

import com.leoschulmann.roboquote.quoteservice.services.NameGeneratingService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/name")
public class NamingController {

    final
    NameGeneratingService generatingService;

    public NamingController(@Qualifier(value = "nameGeneratingServiceImpl") NameGeneratingService generatingService) {
        this.generatingService = generatingService;
    }

    @GetMapping
    public ResponseEntity<String> generateName() {
        return new ResponseEntity<>(generatingService.generate(), HttpStatus.OK);
    }
}
