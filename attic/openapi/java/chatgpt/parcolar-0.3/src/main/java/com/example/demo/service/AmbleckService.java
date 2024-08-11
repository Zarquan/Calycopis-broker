package com.example.demo.service;

import java.util.List;
import java.util.ArrayList;

import com.example.demo.model.ParcolarRequest;
import com.example.demo.model.ParcolarResponse;
import org.springframework.stereotype.Service;


import com.example.demo.model.ParcolarOffer;
import com.example.demo.model.ParcolarRequest;
import com.example.demo.model.ParcolarResponse;

import com.example.demo.model.AbstractExecutable;

import com.example.demo.model.PingExecutable;
import com.example.demo.model.PingSpecific;

import com.example.demo.model.DelayExecutable;
import com.example.demo.model.DelaySpecific;

import com.example.demo.model.Resources;

import com.example.demo.model.AbstractComputeResource;
import com.example.demo.model.SimpleComputeResource;
import com.example.demo.model.SimpleComputeSpecific;

import com.example.demo.model.AbstractStorageResource;
import com.example.demo.model.SimpleStorageResource;
import com.example.demo.model.SimpleStorageSpecific;


@Service
public class AmbleckService {

    public ParcolarResponse handleRequest(ParcolarRequest request) {
        // Implement your business logic here
        return response(
            request
            );
        }

    public ParcolarResponse response(ParcolarRequest request)
        {
	    ParcolarResponse response = new ParcolarResponse();

        List<ParcolarOffer> offers = offers(
            request
            );
        response.setOffers(
            offers
            );

        if (offers.size() > 0)
            {
            response.setResult(
                "YES"
                );
	        }
        else {
	        response.setResult(
	            "NO"
	            );
            }

        return response ;
        }

    public List<ParcolarOffer> offers(ParcolarRequest request)
        {
        List<ParcolarOffer> offers = new ArrayList<ParcolarOffer>();

        AbstractExecutable executable = executable(
            request.getExecutable()
            );

        if (executable != null)
            {
            ParcolarOffer offer = new ParcolarOffer();
            offer.setExecutable(
                executable
                );
            offers.add(
                offer
                );
            }

        return offers ;
        }

    public AbstractExecutable executable(AbstractExecutable requested)
        {
        if (requested instanceof PingExecutable)
            {
            return executable(
                (PingExecutable) requested
                );
            }
        else if (requested instanceof DelayExecutable)
            {
            return executable(
                (DelayExecutable) requested
                );
            }
        else {
            // Unknown executable.
            return null ;
            }
        }

    public PingExecutable executable(PingExecutable requested)
        {
        PingExecutable result = new PingExecutable();
        result.setType(
            "urn:ping-executable"
            );
        result.setName(
            requested.getName()
            );
        PingSpecific spec = new PingSpecific();
        spec.setTarget(
            requested.getSpec().getTarget()
            );
        result.setSpec(
            spec
            );
        return result;
        }

    public DelayExecutable executable(DelayExecutable requested)
        {
        DelayExecutable result = new DelayExecutable();
        result.setType(
            "urn:delay-executable"
            );
        result.setName(
            requested.getName()
            );
        DelaySpecific spec = new DelaySpecific();
        spec.setDuration(
            requested.getSpec().getDuration()
            );
        result.setSpec(
            spec
            );
        return result;
        }
    }

