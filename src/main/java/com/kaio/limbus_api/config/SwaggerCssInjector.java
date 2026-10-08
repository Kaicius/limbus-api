package com.kaio.limbus_api.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class SwaggerCssInjector extends SwaggerIndexPageTransformer {

    public SwaggerCssInjector(SwaggerUiConfigProperties swaggerUiConfig,
                              SwaggerUiOAuthProperties swaggerUiOAuthProperties,
                              SwaggerWelcomeCommon swaggerWelcomeCommon,
                              ObjectMapperProvider objectMapperProvider) {
        super(swaggerUiConfig, swaggerUiOAuthProperties, swaggerWelcomeCommon, objectMapperProvider);
    }

    @Override
    public @NonNull Resource transform(@NonNull HttpServletRequest request,
                                       @NonNull Resource resource,
                                       @NonNull ResourceTransformerChain transformer) throws IOException {
        if ("index.html".equals(resource.getFilename())) {
            try (InputStream in = resource.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String html = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                String transformado = html.replace("<title>Swagger UI</title>", "<title>Limbus API</title>")
                        // remove os favicons padrão do Swagger e usa o logo da Limbus Company
                        .replaceAll("<link[^>]*rel=\"icon\"[^>]*>", "")
                        .replace("</head>",
                        "<link rel=\"icon\" type=\"image/png\" href=\"/limbus-logo.png\" />"
                        + "<link rel=\"stylesheet\" type=\"text/css\" href=\"/swagger-theme.css\" /></head>");
                return new TransformedResource(resource, transformado.getBytes());
            }
        }
        return super.transform(request, resource, transformer);
    }
}
