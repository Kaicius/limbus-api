package com.kaio.limbus_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Corpo padrão devolvido em qualquer erro da API")
public record ErrorResponse(
        @Schema(description = "Código de status HTTP", example = "404")
        int status,

        @Schema(description = "Nome curto do erro HTTP", example = "Not Found")
        String erro,

        @Schema(description = "Explicação do que aconteceu", example = "Sinner não encontrado(a) com id: 99")
        String mensagem,

        @Schema(description = "Momento em que o erro ocorreu")
        LocalDateTime timestamp
) {
}