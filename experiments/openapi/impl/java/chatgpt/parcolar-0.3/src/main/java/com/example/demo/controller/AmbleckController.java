package com.example.demo.controller;

import com.example.demo.model.ParcolarRequest;
import com.example.demo.model.ParcolarResponse;
import com.example.demo.service.AmbleckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ambleck")
public class AmbleckController {

    @Autowired
    private AmbleckService ambleckService;

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/yaml"},
                 produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/yaml"})
    public ParcolarResponse ambleckPost(@Validated @RequestBody ParcolarRequest request) {
        return ambleckService.handleRequest(request);
    }
}

