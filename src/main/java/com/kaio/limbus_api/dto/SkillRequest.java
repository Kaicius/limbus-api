package com.kaio.limbus_api.dto;

import com.kaio.limbus_api.entity.Skill;
import com.kaio.limbus_api.enums.Sin;
import com.kaio.limbus_api.enums.SkillSlot;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastrar ou atualizar uma Skill. A Identity dona vem na URL, não no corpo.")
public record SkillRequest(

        @Schema(description = "Posição da Skill: SKILL_1, SKILL_2, SKILL_3 ou DEFESA", example = "SKILL_1")
        @NotNull
        SkillSlot slot,

        @Schema(description = "Número da variante da Skill (começa em 1)", example = "1", minimum = "1", maximum = "10")
        @Min(1) @Max(10)
        int variante,

        @Schema(description = "Sin da Skill: WRATH, LUST, SLOTH, GLOOM, GLUTTONY, ENVY ou PRIDE", example = "GLUTTONY")
        @NotNull
        Sin sin,

        @Schema(description = "Nome da Skill", example = "Enwrap 330 times in Long Swat...", maxLength = 255)
        @NotBlank @Size(max = 255)
        @Pattern(regexp = "^(?=.*\\p{L}).+$", message = "deve conter letras (não pode ser só números ou símbolos)")
        String nome,

        @Schema(description = "Quantidade de moedas normais da Skill", example = "2", minimum = "1", maximum = "10")
        @Min(1) @Max(10)
        int quantidadeCoins,

        @Schema(description = "Descrição do efeito da Skill", example = "(75 - (Karmic_Consequence/2))% chance to produce a Blunt weapon - Coins using Blunt weapons deal +15% damage If this Skill is marked with Mark Of The Prescript of the Prescript, +1 Clash Power and deal +20% damage +1 Coin Power for every 6 (Poise on self + Sinking on target) (max 2) [On Use] Convert Coins equal to (unlock stage - 1) into Unbreakable Coin, beginning with the final Coin [On Use] Gain +2 Poise Count 2coin[On Hit] Gain 2 Poise 2coin[On Hit without cracking] Inflict 2 sinking", maxLength = 2000)
        @NotBlank @Size(max = 2000)
        String descricaoEfeito
) {
    public Skill toEntity() {
        return new Skill(slot, variante, sin, nome, quantidadeCoins, descricaoEfeito, null);
    }
}
