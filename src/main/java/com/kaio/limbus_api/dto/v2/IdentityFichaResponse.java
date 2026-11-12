package com.kaio.limbus_api.dto.v2;

import com.kaio.limbus_api.entity.Identity;
import com.kaio.limbus_api.entity.IdentityStats;
import com.kaio.limbus_api.entity.Passive;
import com.kaio.limbus_api.entity.Sanity;
import com.kaio.limbus_api.entity.Skill;
import com.kaio.limbus_api.entity.Tag;
import com.kaio.limbus_api.enums.PassivaType;
import com.kaio.limbus_api.enums.Rarity;
import com.kaio.limbus_api.enums.Resistance;
import com.kaio.limbus_api.enums.Sin;
import com.kaio.limbus_api.enums.SkillSlot;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Ficha completa de uma Identity (versão 2 da API)")
public record IdentityFichaResponse(
        @Schema(description = "Identificador da Identity", example = "1")
        long id,
        @Schema(description = "Nome da Identity", example = "[The House of Spiders: The Thumb Nursefather] Rodion")
        String nome,
        @Schema(description = "Uptie (1 a 4)", example = "4")
        int uptie,
        @Schema(description = "Raridade", example = "ZERO_ZERO_ZERO")
        Rarity rarity,
        @Schema(description = "Sinner dono da Identity")
        SinnerResumo sinner,
        @Schema(description = "Nomes das Tags, em ordem alfabética", example = "[\"The House of Spiders\"]")
        List<String> tags,
        @Schema(description = "Stats da Identity (null se ainda não cadastrados)")
        StatsResumo stats,
        @Schema(description = "Sanity da Identity (null se ainda não cadastrada)")
        SanityResumo sanity,
        @Schema(description = "Skills da Identity, ordenadas por slot e variante")
        List<SkillResumo> skills,
        @Schema(description = "Passivas da Identity")
        List<PassiveResumo> passives
) {

    @Schema(description = "Sinner resumido (v2): só id e nome")
    public record SinnerResumo(
            @Schema(description = "Identificador do Sinner", example = "9") long id,
            @Schema(description = "Nome do Sinner", example = "Rodion") String nome) {
    }

    @Schema(description = "Stats da Identity (v2), sem a Identity aninhada")
    public record StatsResumo(
            @Schema(description = "Identificador dos Stats", example = "1") long id,
            @Schema(description = "Pontos de vida (1 a 99999)", example = "248") int hp,
            @Schema(description = "Faixa de velocidade, no formato mín-máx", example = "4-7") String speed,
            @Schema(description = "Defesa (0 a 9999)", example = "65") int defense,
            @Schema(description = "Limiar de Stagger (1 a 99999)", example = "149") int staggerThreshold,
            @Schema(description = "Resistência a Slash", example = "INEFFECTIVE") Resistance resistanceSlash,
            @Schema(description = "Resistência a Pierce", example = "NORMAL") Resistance resistancePierce,
            @Schema(description = "Resistência a Blunt", example = "FATAL") Resistance resistanceBlunt) {
    }

    @Schema(description = "Sanity da Identity (v2), sem a Identity aninhada")
    public record SanityResumo(
            @Schema(description = "Identificador da Sanity", example = "1") long id,
            @Schema(description = "O que acontece no Panic", example = "Does not act for this turn") String panicType,
            @Schema(description = "O que aumenta a Sanity", example = "Increase by 10 after this unit defeats an enemy") String increasingFactors,
            @Schema(description = "O que diminui a Sanity", example = "Decrease by 5 when an ally is defeated") String decreasingFactors) {
    }

    @Schema(description = "Skill da Identity (v2), sem a Identity aninhada")
    public record SkillResumo(
            @Schema(description = "Identificador da Skill", example = "1") long id,
            @Schema(description = "Slot da Skill", example = "SKILL_1") SkillSlot slot,
            @Schema(description = "Variante dentro do slot (1 a 10)", example = "1") int variante,
            @Schema(description = "Pecado (afinidade) da Skill", example = "PRIDE") Sin sin,
            @Schema(description = "Nome da Skill", example = "Colpi di Taglio") String nome,
            @Schema(description = "Quantidade de moedas (1 a 10)", example = "2") int quantidadeCoins,
            @Schema(description = "Descrição do efeito", example = "[On Hit] Gain 2 Poise") String descricaoEfeito) {
    }

    @Schema(description = "Passiva da Identity (v2), sem a Identity aninhada")
    public record PassiveResumo(
            @Schema(description = "Identificador da Passiva", example = "1") long id,
            @Schema(description = "Tipo da Passiva", example = "BATTLE") PassivaType tipo,
            @Schema(description = "Nome da Passiva", example = "The Eye of Precognition") String nome,
            @Schema(description = "Descrição do efeito", example = "[Turn Start] Gain 1 Prescript") String descricao) {
    }

    public static IdentityFichaResponse de(Identity identity, IdentityStats stats, Sanity sanity,
                                           List<Skill> skills, List<Passive> passives) {
        return new IdentityFichaResponse(
                identity.getId(),
                identity.getNome(),
                identity.getUptie(),
                identity.getRarity(),
                new SinnerResumo(identity.getSinner().getId(), identity.getSinner().getNome()),
                identity.getTags().stream().map(Tag::getNome).sorted().toList(),
                stats == null ? null : new StatsResumo(stats.getId(), stats.getHp(), stats.getSpeed(),
                        stats.getDefense(), stats.getStaggerThreshold(),
                        stats.getResistanceSlash(), stats.getResistancePierce(), stats.getResistanceBlunt()),
                sanity == null ? null : new SanityResumo(sanity.getId(), sanity.getPanicType(),
                        sanity.getIncreasingFactors(), sanity.getDecreasingFactors()),
                skills.stream()
                        .map(s -> new SkillResumo(s.getId(), s.getSlot(), s.getVariante(), s.getSin(),
                                s.getNome(), s.getQuantidadeCoins(), s.getDescricaoEfeito()))
                        .toList(),
                passives.stream()
                        .map(p -> new PassiveResumo(p.getId(), p.getTipo(), p.getNome(), p.getDescricao()))
                        .toList());
    }
}
