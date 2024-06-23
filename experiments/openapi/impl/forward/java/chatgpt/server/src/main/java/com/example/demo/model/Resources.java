package com.example.demo.model;

import java.util.List;

public class Resources {
    private List<AbstractComputeResource> compute;
    private List<AbstractStorageResource> storage;

    public List<AbstractComputeResource> getCompute() {
        return compute;
    }

    public void setCompute(List<AbstractComputeResource> compute) {
        this.compute = compute;
    }

    public List<AbstractStorageResource> getStorage() {
        return storage;
    }

    public void setStorage(List<AbstractStorageResource> storage) {
        this.storage = storage;
    }
}

