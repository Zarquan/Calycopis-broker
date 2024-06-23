package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PingExecutable.class, name = "urn:ping-executable"),
        @JsonSubTypes.Type(value = DelayExecutable.class, name = "urn:delay-executable")
})
public abstract class AbstractExecutable {
    private String type;
    private String name;
    private AbstractSpecific spec;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AbstractSpecific getSpec() {
        return spec;
    }

    public void setSpec(AbstractSpecific spec) {
        this.spec = spec;
    }
}

