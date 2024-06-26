package com.example.demo.model;

public class SimpleComputeSpecific extends AbstractSpecific {
    private MinMaxInteger cores;
    private MinMaxInteger memory;

    public MinMaxInteger getCores() {
        return cores;
    }

    public void setCores(MinMaxInteger cores) {
        this.cores = cores;
    }

    public MinMaxInteger getMemory() {
        return memory;
    }

    public void setMemory(MinMaxInteger memory) {
        this.memory = memory;
    }
}

