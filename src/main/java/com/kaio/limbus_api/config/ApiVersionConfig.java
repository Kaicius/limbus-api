package com.kaio.limbus_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiVersionConfig implements WebMvcConfigurer {

    public static final String HEADER = "X-API-Version";
    public static final String VERSAO_PADRAO = "1";

    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer
                .useRequestHeader(HEADER)
                .setDefaultVersion(VERSAO_PADRAO);
    }
}
