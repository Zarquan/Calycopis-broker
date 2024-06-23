package com.example.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

@Component
public class YamlJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {

    public YamlJackson2HttpMessageConverter() {
        super(
            new ObjectMapper(
                new YAMLFactory().disable(
                        YAMLGenerator.Feature.WRITE_DOC_START_MARKER
                    ).disable(
                        YAMLGenerator.Feature.USE_NATIVE_OBJECT_ID
                    ).disable(
                        YAMLGenerator.Feature.USE_NATIVE_TYPE_ID
                    )
                ),
            MediaType.parseMediaType(
                "application/yaml"
                )
            );
        }
    }

