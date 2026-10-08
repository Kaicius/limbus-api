package com.kaio.limbus_api.dto;

import com.kaio.limbus_api.entity.Sanity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastrar ou atualizar a Sanity de uma Identity. A Identity dona vem na URL, não no corpo.")
public record SanityRequest(

        @Schema(description = "Descrição do Panic da Identity", example = "· Panic Effects Does not act for this turn", maxLength = 2000)
        @NotBlank @Size(max = 2000)
        String panicType,

        @Schema(description = "O que faz a Sanity subir", example = "· Increases after winning a Clash based on the Clash count (Base Value is 10, raised by 20% per Clash count after 1) · Increase by 10 after this unit defeats an enemy regardless of their level · Increase by 5 after an ally defeats an enemy regardless of their level", maxLength = 2000)
        @NotBlank @Size(max = 2000)
        String increasingFactors,

        @Schema(description = "O que faz a Sanity cair", example = "· If the level of the defeated ally was higher than or equal to this unit's, decrease based on the level difference (Base Value is 10, raised by 10 per level)", maxLength = 2000)
        @NotBlank @Size(max = 2000)
        String decreasingFactors
) {
    public Sanity toEntity() {
        return new Sanity(panicType, increasingFactors, decreasingFactors, null);
    }
}
