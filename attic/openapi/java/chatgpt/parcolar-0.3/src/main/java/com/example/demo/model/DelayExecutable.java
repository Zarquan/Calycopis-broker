package com.example.demo.model;

public class DelayExecutable extends AbstractExecutable {
    private DelaySpecific spec;

    @Override
    public DelaySpecific getSpec() {
        return spec;
    }

    public void setSpec(DelaySpecific spec) {
        this.spec = spec;
    }
}

