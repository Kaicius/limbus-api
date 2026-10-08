package com.kaio.limbus_api.dto;

import com.kaio.limbus_api.entity.Identity;
import com.kaio.limbus_api.enums.Rarity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.Set;

@Schema(description = "Dados para cadastrar ou atualizar uma Identity. O Sinner dono vem na URL, não no corpo.")
public record IdentityRequest(

        @Schema(description = "Nome da Identity (único), de 2 a 100 caracteres", example = "[The House of Spiders: The Index Nursefather] Yi Sang", minLength = 2, maxLength = 100)
        @NotBlank @Size(min = 2, max = 100)
        @Pattern(regexp = "^(?=.*\\p{L}).+$", message = "deve conter letras (não pode ser só números ou símbolos)")
        String nome,

        @Schema(description = "Nível de uptie dos dados cadastrados (1 a 4)", example = "4", minimum = "1", maximum = "4")
        @Min(1) @Max(4)
        int uptie,

        @Schema(description = "Raridade: ZERO (0), ZERO_ZERO (00) ou ZERO_ZERO_ZERO (000)", example = "ZERO_ZERO_ZERO")
        @NotNull
        Rarity rarity,

        @Schema(description = "Ids das Tags da Identity (ao menos uma). As Tags precisam existir antes.", example = "[1, 2, 3]")
        @NotEmpty @Size(min = 1, max = 10)
        Set<@NotNull Long> tagIds
) {
    public Identity toEntity() {
        return new Identity(nome, uptie, rarity, null);
    }
}