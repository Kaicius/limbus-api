package com.kaio.limbus_api.filter;

import com.kaio.limbus_api.config.ApiVersionConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LimbusCorsFilter extends CorsFilter {

    public LimbusCorsFilter(@Value("${app.cors.allowed-origins:http://localhost:4200}") List<String> origensPermitidas) {
        super(configurar(origensPermitidas));
    }

    private static UrlBasedCorsConfigurationSource configurar(List<String> origensPermitidas) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origensPermitidas);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Content-Type", "Accept",
                IdempotencyFilter.HEADER, ApiVersionConfig.HEADER));
        config.setExposedHeaders(List.of("Location", "Retry-After",
                RateLimitFilter.HEADER_LIMITE, RateLimitFilter.HEADER_RESTANTE,
                IdempotencyFilter.HEADER_REPLAY));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
