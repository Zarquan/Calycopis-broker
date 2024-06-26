package com.example.demo.model;

public class SimpleStorageResource extends AbstractStorageResource {
    private SimpleStorageSpecific spec;

    @Override
    public SimpleStorageSpecific getSpec() {
        return spec;
    }

    public void setSpec(SimpleStorageSpecific spec) {
        this.spec = spec;
    }
}

