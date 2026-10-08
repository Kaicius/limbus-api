package com.kaio.limbus_api.dto;

import com.kaio.limbus_api.entity.IdentityStats;
import com.kaio.limbus_api.enums.Resistance;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Dados para cadastrar ou atualizar os Stats de uma Identity. A Identity dona vem na URL, não no corpo.")
public record IdentityStatsRequest(

        @Schema(description = "Pontos de vida (HP)", example = "248", minimum = "1", maximum = "99999")
        @Min(1) @Max(99999)
        int hp,

        @Schema(description = "Faixa de velocidade no formato mín-máx (nunca um valor único)", example = "3-8", minLength = 3, maxLength = 7, pattern = "^[1-9]\\d*-[1-9]\\d*$")
        @NotBlank @Size(min = 3, max = 7)
        @Pattern(regexp = "^[1-9]\\d*-[1-9]\\d*$", message = "deve estar no formato mín-máx, ex.: 3-7")
        String speed,

        @Schema(description = "Defesa", example = "67", minimum = "0", maximum = "9999")
        @Min(0) @Max(9999)
        int defense,

        @Schema(description = "Limiar de Stagger", example = "161", minimum = "1", maximum = "99999")
        @Min(1) @Max(99999)
        int staggerThreshold,

        @Schema(description = "Resistência a ataques Slash: INEFFECTIVE (Ineficaz, 0.5x), NORMAL (Neutro, 1.0x), WEAK (Fraco, 1.5x) ou FATAL (Fatal, 2.0x)", example = "INEFFECTIVE")
        @NotNull
        Resistance resistanceSlash,

        @Schema(description = "Resistência a ataques Pierce: INEFFECTIVE (Ineficaz, 0.5x), NORMAL (Neutro, 1.0x), WEAK (Fraco, 1.5x) ou FATAL (Fatal, 2.0x)", example = "FATAL")
        @NotNull
        Resistance resistancePierce,

        @Schema(description = "Resistência a ataques Blunt: INEFFECTIVE (Ineficaz, 0.5x), NORMAL (Neutro, 1.0x), WEAK (Fraco, 1.5x) ou FATAL (Fatal, 2.0x)", example = "NORMAL")
        @NotNull
        Resistance resistanceBlunt
) {
    public IdentityStats toEntity() {
        return new IdentityStats(hp, speed, defense, staggerThreshold,
                resistanceSlash, resistancePierce, resistanceBlunt, null);
    }
}
