package com.example.demo.model;

public class PingExecutable extends AbstractExecutable {
    private PingSpecific spec;

    @Override
    public PingSpecific getSpec() {
        return spec;
    }

    public void setSpec(PingSpecific spec) {
        this.spec = spec;
    }
}

