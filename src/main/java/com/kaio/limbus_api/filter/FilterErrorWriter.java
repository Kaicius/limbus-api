package com.kaio.limbus_api.filter;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

final class FilterErrorWriter {

    private FilterErrorWriter() {
    }

    static void escrever(HttpServletResponse response, int status, String erro, String mensagem) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"status\":" + status
                + ",\"erro\":\"" + escapar(erro) + "\""
                + ",\"mensagem\":\"" + escapar(mensagem) + "\""
                + ",\"timestamp\":\"" + LocalDateTime.now() + "\"}");
    }

    private static String escapar(String texto) {
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
