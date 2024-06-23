package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final YamlJackson2HttpMessageConverter yamlJackson2HttpMessageConverter;

    public WebConfig(YamlJackson2HttpMessageConverter yamlJackson2HttpMessageConverter) {
        this.yamlJackson2HttpMessageConverter = yamlJackson2HttpMessageConverter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(yamlJackson2HttpMessageConverter);
    }
}

