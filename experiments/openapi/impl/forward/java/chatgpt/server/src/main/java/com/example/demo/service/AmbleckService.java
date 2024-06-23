package com.example.demo.service;

import com.example.demo.model.ParcolarRequest;
import com.example.demo.model.ParcolarResponse;
import org.springframework.stereotype.Service;

@Service
public class AmbleckService {

    public ParcolarResponse handleRequest(ParcolarRequest request) {
        // Implement your business logic here
        ParcolarResponse response = new ParcolarResponse();
        response.setResult("YES");
        return response;
    }
}

