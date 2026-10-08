package com.kaio.limbus_api.dto;

import com.kaio.limbus_api.entity.Passive;
import com.kaio.limbus_api.enums.PassivaType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastrar ou atualizar uma Passiva. A Identity dona vem na URL, não no corpo.")
public record PassiveRequest(

        @Schema(description = "Tipo da Passiva: BATTLE (combate) ou SUPPORT (suporte)", example = "BATTLE")
        @NotNull
        PassivaType tipo,

        @Schema(description = "Nome da Passiva", example = "Prescript Delivered on a Device", maxLength = 255)
        @NotBlank @Size(max = 255)
        String nome,

        @Schema(description = "Descrição da Passiva", example = "[Turn Start] - Gain Prescript: [Device] I / Prescript: [Device] II / Prescript: [Device] III / Prescript: [Device] IV based on Unlock on self - Inflict The Prescript's Target to a random enemy (in Focused Encounters, a Part) - Apply Mark of the Prescript to Base Attack Skills on this unit's Dashboard (1 per Slot, max 2 Skills) · At Unlock - II+, the effect above prioritizes Skill 3 (prioritizes empowered Skill) - All of the effects above and Prescript execution checks do not trigger when this unit is Staggered, Immobilized, in Panic, or in E.G.O Corrosion", maxLength = 2000)
        @NotBlank @Size(max = 2000)
        String descricao
) {
    public Passive toEntity() {
        return new Passive(tipo, nome, descricao, null);
    }
}
