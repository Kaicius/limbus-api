package com.kaio.limbus_api.entity;

import com.kaio.limbus_api.enums.Sin;
import com.kaio.limbus_api.enums.SkillSlot;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Schema(description = "Skill de uma Identity (ataque ou defesa), com sin, moedas e variante")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único, gerado pelo banco", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Schema(description = "Posição da Skill: SKILL_1, SKILL_2, SKILL_3 ou DEFESA", example = "SKILL_1")
    private SkillSlot slot;

    @Min(1) @Max(10)
    @Schema(description = "Número da variante da Skill (começa em 1)", example = "1", minimum = "1", maximum = "10")
    private int variante;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Schema(description = "Sin da Skill: WRATH, LUST, SLOTH, GLOOM, GLUTTONY, ENVY ou PRIDE", example = "PRIDE")
    private Sin sin;

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Nome da Skill", example = "Colpi di Taglio", maxLength = 255)
    private String nome;

    @Min(1) @Max(10)
    @Schema(description = "Quantidade de moedas da Skill", example = "2", minimum = "1", maximum = "10")
    private int quantidadeCoins;

    @NotBlank
    @Column(length = 2000)
    @Size(max = 2000)
    @Schema(description = "Descrição do efeito da Skill", example = "[On Hit] Gain 2 Poise", maxLength = 2000)
    private String descricaoEfeito;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "identity_id")
    @Schema(description = "Identity dona da Skill (relação N:1, definida pela URL)")
    private Identity identity;

    public Skill() {
    }

    public Skill(SkillSlot slot, int variante, Sin sin,
                 String nome, int quantidadeCoins, String descricaoEfeito,
                 Identity identity) {
        this.slot = slot;
        this.variante = variante;
        this.sin = sin;
        this.nome = nome;
        this.quantidadeCoins = quantidadeCoins;
        this.descricaoEfeito = descricaoEfeito;
        this.identity = identity;
    }

    public long getId() {
        return id;
    }

    public SkillSlot getSlot() {
        return slot;
    }

    public void setSlot(SkillSlot slot) {
        this.slot = slot;
    }

    public int getVariante() {
        return variante;
    }

    public void setVariante(int variante) {
        this.variante = variante;
    }

    public Sin getSin() {
        return sin;
    }

    public void setSin(Sin sin) {
        this.sin = sin;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getQuantidadeCoins() {
        return quantidadeCoins;
    }

    public void setQuantidadeCoins(int quantidadeCoins) {
        this.quantidadeCoins = quantidadeCoins;
    }

    public String getDescricaoEfeito() {
        return descricaoEfeito;
    }

    public void setDescricaoEfeito(String descricaoEfeito) {
        this.descricaoEfeito = descricaoEfeito;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }
}
