package com.kaio.limbus_api.dto;

import com.kaio.limbus_api.entity.ApiKey;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Resposta da geração de uma chave de API. É a única vez em que o valor completo da chave aparece")
public record ApiKeyCriadaResponse(
        @Schema(description = "Identificador da chave", example = "1")
        long id,
        @Schema(description = "Nome do dono da chave", example = "Kaio Alves")
        String nome,
        @Schema(description = "E-mail do dono da chave", example = "kaio@exemplo.com")
        String email,
        @Schema(description = "Valor completo da chave, para enviar no cabeçalho X-API-Key. Guarde agora: não é possível consultá-lo depois",
                example = "lmb_Xk3v9Qa2Lr8TnW5cYb1ZpD4eFg6HjK0mNs7UvA")
        String chave,
        @Schema(description = "Primeiros caracteres da chave", example = "lmb_Xk3v9Qa2")
        String prefixo,
        @Schema(description = "Quando a chave foi gerada")
        LocalDateTime criadaEm
) {

    public static ApiKeyCriadaResponse de(ApiKey apiKey, String chave) {
        return new ApiKeyCriadaResponse(apiKey.getId(), apiKey.getNome(), apiKey.getEmail(), chave,
                apiKey.getPrefixo(), apiKey.getCriadaEm());
    }
}
