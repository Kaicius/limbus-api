package com.kaio.limbus_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Cadastro para gerar uma chave de API")
public record ApiKeyRequest(
        @NotBlank
        @Size(min = 2, max = 100)
        @Schema(description = "Nome de quem vai usar a chave (pessoa ou aplicação)", example = "Kaio Alves",
                minLength = 2, maxLength = 100)
        String nome,

        @NotBlank
        @Email
        @Size(max = 150)
        @Schema(description = "E-mail de contato do dono da chave", example = "kaio@exemplo.com", maxLength = 150)
        String email
) {
}
