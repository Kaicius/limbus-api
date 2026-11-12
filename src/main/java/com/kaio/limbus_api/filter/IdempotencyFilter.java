package com.kaio.limbus_api.filter;

import com.kaio.limbus_api.config.ApiVersionConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class IdempotencyFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Idempotency-Key";
    public static final String HEADER_REPLAY = "Idempotent-Replayed";
    private static final int TAMANHO_MAXIMO = 255;
    private static final Duration VALIDADE = Duration.ofHours(24);

    private record Entrada(byte[] hashDoCorpo, Instant criadaEm, RespostaGuardada resposta) {
        boolean expirada() {
            return criadaEm.plus(VALIDADE).isBefore(Instant.now());
        }
    }

    private record RespostaGuardada(int status, String contentType, String location, byte[] corpo) {
    }

    private final Map<String, Entrada> entradas = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"POST".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String chave = request.getHeader(HEADER);
        if (chave == null || chave.isBlank()) {
            FilterErrorWriter.escrever(response, 400, "Bad Request",
                    "Cabeçalho obrigatório ausente: " + HEADER + " (envie uma chave única por operação, ex.: um UUID)");
            return;
        }
        chave = chave.trim();
        if (chave.length() > TAMANHO_MAXIMO) {
            FilterErrorWriter.escrever(response, 400, "Bad Request",
                    HEADER + " deve ter no máximo " + TAMANHO_MAXIMO + " caracteres");
            return;
        }

        String chaveCompleta = request.getRequestURI() + "|" + versaoDaApi(request) + "|" + chave;

        Entrada existente = entradas.get(chaveCompleta);
        if (existente != null && existente.expirada()) {
            entradas.remove(chaveCompleta, existente);
            existente = null;
        }

        if (existente != null) {
            byte[] hash = sha256(request.getInputStream().readAllBytes());
            if (!Arrays.equals(hash, existente.hashDoCorpo())) {
                FilterErrorWriter.escrever(response, 422, "Unprocessable Content",
                        HEADER + " já foi usada com um corpo de requisição diferente");
            } else if (existente.resposta() == null) {
                FilterErrorWriter.escrever(response, 409, "Conflict",
                        "Uma requisição com esta " + HEADER + " ainda está sendo processada");
            } else {
                repetir(response, existente.resposta());
            }
            return;
        }

        byte[] corpo = request.getInputStream().readAllBytes();
        Entrada emAndamento = new Entrada(sha256(corpo), Instant.now(), null);

        if (entradas.putIfAbsent(chaveCompleta, emAndamento) != null) {
            FilterErrorWriter.escrever(response, 409, "Conflict",
                    "Uma requisição com esta " + HEADER + " ainda está sendo processada");
            return;
        }

        removerExpiradas();

        ContentCachingResponseWrapper respostaCapturada = new ContentCachingResponseWrapper(response);
        boolean guardada = false;
        try {
            chain.doFilter(new RequisicaoComCorpo(request, corpo), respostaCapturada);

            int status = respostaCapturada.getStatus();
            if (status >= 200 && status < 300) {
                RespostaGuardada resultado = new RespostaGuardada(
                        status,
                        respostaCapturada.getContentType(),
                        respostaCapturada.getHeader(HttpHeaders.LOCATION),
                        respostaCapturada.getContentAsByteArray());
                entradas.replace(chaveCompleta, emAndamento,
                        new Entrada(emAndamento.hashDoCorpo(), emAndamento.criadaEm(), resultado));
                guardada = true;
            }
        } finally {
            if (!guardada) {
                entradas.remove(chaveCompleta, emAndamento);
            }
            respostaCapturada.copyBodyToResponse();
        }
    }

    private void repetir(HttpServletResponse response, RespostaGuardada guardada) throws IOException {
        response.setStatus(guardada.status());
        if (guardada.contentType() != null) {
            response.setContentType(guardada.contentType());
        }
        if (guardada.location() != null) {
            response.setHeader(HttpHeaders.LOCATION, guardada.location());
        }
        response.setHeader(HEADER_REPLAY, "true");
        response.getOutputStream().write(guardada.corpo());
    }

    private static String versaoDaApi(HttpServletRequest request) {
        String versao = request.getHeader(ApiVersionConfig.HEADER);
        return versao == null ? ApiVersionConfig.VERSAO_PADRAO : versao.trim();
    }

    private void removerExpiradas() {
        entradas.values().removeIf(entrada -> entrada.resposta() != null && entrada.expirada());
    }

    private static byte[] sha256(byte[] dados) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(dados);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static class RequisicaoComCorpo extends HttpServletRequestWrapper {
        private final byte[] corpo;

        RequisicaoComCorpo(HttpServletRequest request, byte[] corpo) {
            super(request);
            this.corpo = corpo;
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream in = new ByteArrayInputStream(corpo);
            return new ServletInputStream() {
                @Override
                public int read() {
                    return in.read();
                }

                @Override
                public int read(byte[] b, int off, int len) {
                    return in.read(b, off, len);
                }

                @Override
                public boolean isFinished() {
                    return in.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener listener) {
                    throw new UnsupportedOperationException();
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
        }
    }
}
