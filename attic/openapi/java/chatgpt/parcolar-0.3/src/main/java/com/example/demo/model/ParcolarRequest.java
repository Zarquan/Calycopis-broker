package com.example.demo.model;

public class ParcolarRequest {
    private AbstractExecutable executable;
    private Resources resources;

    public AbstractExecutable getExecutable() {
        return executable;
    }

    public void setExecutable(AbstractExecutable executable) {
        this.executable = executable;
    }

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }
}

