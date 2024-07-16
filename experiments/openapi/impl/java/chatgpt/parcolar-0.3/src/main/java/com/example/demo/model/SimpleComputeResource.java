package com.example.demo.model;

public class SimpleComputeResource extends AbstractComputeResource {
    private SimpleComputeSpecific spec;

    @Override
    public SimpleComputeSpecific getSpec() {
        return spec;
    }

    public void setSpec(SimpleComputeSpecific spec) {
        this.spec = spec;
    }
}

