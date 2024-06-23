package com.example.demo.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class ParcolarResponse {
    private String result;

    @JacksonXmlElementWrapper(localName = "offers")
    @JacksonXmlProperty(localName = "offer")
    private List<ParcolarOffer> offers;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<ParcolarOffer> getOffers() {
        return offers;
    }

    public void setOffers(List<ParcolarOffer> offers) {
        this.offers = offers;
    }
}

