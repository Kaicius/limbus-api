package com.kaio.limbus_api.dto;

import com.kaio.limbus_api.entity.ApiKey;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Chave de API cadastrada. O valor da chave não é guardado, só o seu hash; por isso aqui aparece apenas o prefixo")
public record ApiKeyResponse(
        @Schema(description = "Identificador da chave", example = "1")
        long id,
        @Schema(description = "Nome do dono da chave", example = "Kaio Alves")
        String nome,
        @Schema(description = "E-mail do dono da chave", example = "kaio@exemplo.com")
        String email,
        @Schema(description = "Primeiros caracteres da chave, para reconhecê-la", example = "lmb_Xk3v9Qa2")
        String prefixo,
        @Schema(description = "false quando a chave foi revogada e não é mais aceita", example = "true")
        boolean ativa,
        @Schema(description = "Quando a chave foi gerada")
        LocalDateTime criadaEm,
        @Schema(description = "Última vez em que a chave autenticou uma requisição (null se nunca foi usada)")
        LocalDateTime ultimoUso
) {

    public static ApiKeyResponse de(ApiKey apiKey) {
        return new ApiKeyResponse(apiKey.getId(), apiKey.getNome(), apiKey.getEmail(), apiKey.getPrefixo(),
                apiKey.isAtiva(), apiKey.getCriadaEm(), apiKey.getUltimoUso());
    }
}
