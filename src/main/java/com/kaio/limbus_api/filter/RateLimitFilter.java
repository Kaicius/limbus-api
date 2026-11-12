package com.kaio.limbus_api.filter;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class RateLimitFilter extends OncePerRequestFilter {

    public static final String HEADER_LIMITE = "X-Rate-Limit-Limit";
    public static final String HEADER_RESTANTE = "X-Rate-Limit-Remaining";

    private final long capacidade;
    private final Duration janela;

    private final Map<String, Bucket> baldes = new ConcurrentHashMap<>();

    public RateLimitFilter(@Value("${app.rate-limit.capacity:20}") long capacidade,
                           @Value("${app.rate-limit.window-seconds:60}") long janelaEmSegundos) {
        this.capacidade = capacidade;
        this.janela = Duration.ofSeconds(janelaEmSegundos);
    }

    private Bucket novoBalde() {
        return Bucket.builder()
                .addLimit(limit -> limit
                        .capacity(capacidade)
                        .refillGreedy(capacidade, janela))
                .build();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String caminho = request.getRequestURI();
        return "OPTIONS".equalsIgnoreCase(request.getMethod())
                || caminho.startsWith("/swagger-ui")
                || caminho.startsWith("/v3/api-docs")
                || caminho.equals("/swagger-theme.css")
                || caminho.equals("/limbus-logo.png")
                || caminho.equals("/favicon.ico");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Bucket balde = baldes.computeIfAbsent(request.getRemoteAddr(), ip -> novoBalde());
        ConsumptionProbe resultado = balde.tryConsumeAndReturnRemaining(1);

        response.setHeader(HEADER_LIMITE, String.valueOf(capacidade));

        if (resultado.isConsumed()) {
            response.setHeader(HEADER_RESTANTE, String.valueOf(resultado.getRemainingTokens()));
            chain.doFilter(request, response);
        } else {
            long esperar = TimeUnit.NANOSECONDS.toSeconds(resultado.getNanosToWaitForRefill()) + 1;
            response.setHeader(HEADER_RESTANTE, "0");
            response.setHeader("Retry-After", String.valueOf(esperar));
            FilterErrorWriter.escrever(response, 429, "Too Many Requests",
                    "Limite de " + capacidade + " requisições a cada " + janela.toSeconds()
                            + "s excedido. Tente novamente em " + esperar + "s");
        }
    }
}
