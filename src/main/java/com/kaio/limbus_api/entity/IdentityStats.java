package com.kaio.limbus_api.entity;

import com.kaio.limbus_api.enums.Resistance;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Schema(description = "Atributos de combate de uma Identity (HP, velocidade, defesa, Stagger e resistências)")
public class IdentityStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único, gerado pelo banco", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @Min(1) @Max(99999)
    @Schema(description = "Pontos de vida (HP)", example = "248", minimum = "1", maximum = "99999")
    private int hp;

    @NotBlank
    @Size(min = 3, max = 7)
    @Pattern(regexp = "^[1-9]\\d*-[1-9]\\d*$", message = "deve estar no formato mín-máx, ex.: 3-7")
    @Schema(description = "Faixa de velocidade no formato mín-máx (nunca um valor único)", example = "4-7", minLength = 3, maxLength = 7, pattern = "^[1-9]\\d*-[1-9]\\d*$")
    private String speed;

    @Min(0) @Max(9999)
    @Schema(description = "Defesa", example = "65", minimum = "0", maximum = "9999")
    private int defense;

    @Min(1) @Max(99999)
    @Schema(description = "Limiar de Stagger", example = "149", minimum = "1", maximum = "99999")
    private int staggerThreshold;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Schema(description = "Resistência a Slash: INEFFECTIVE (0.5x), NORMAL (1.0x), WEAK (1.5x) ou FATAL (2.0x)", example = "INEFFECTIVE")
    private Resistance resistanceSlash;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Schema(description = "Resistência a Pierce: INEFFECTIVE (0.5x), NORMAL (1.0x), WEAK (1.5x) ou FATAL (2.0x)", example = "NORMAL")
    private Resistance resistancePierce;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Schema(description = "Resistência a Blunt: INEFFECTIVE (0.5x), NORMAL (1.0x), WEAK (1.5x) ou FATAL (2.0x)", example = "FATAL")
    private Resistance resistanceBlunt;

    @NotNull
    @OneToOne
    @JoinColumn(name = "identity_id", unique = true)
    @Schema(description = "Identity dona destes Stats (relação 1:1, definida pela URL)")
    private Identity identity;

    public IdentityStats() {
    }

    public IdentityStats(int hp, String speed, int defense, int staggerThreshold,
                         Resistance resistanceSlash, Resistance resistancePierce, Resistance resistanceBlunt,
                         Identity identity) {
        this.hp = hp;
        this.speed = speed;
        this.defense = defense;
        this.staggerThreshold = staggerThreshold;
        this.resistanceSlash = resistanceSlash;
        this.resistancePierce = resistancePierce;
        this.resistanceBlunt = resistanceBlunt;
        this.identity = identity;
    }

    public long getId() {
        return id;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getStaggerThreshold() {
        return staggerThreshold;
    }

    public void setStaggerThreshold(int staggerThreshold) {
        this.staggerThreshold = staggerThreshold;
    }

    public Resistance getResistanceSlash() {
        return resistanceSlash;
    }

    public void setResistanceSlash(Resistance resistanceSlash) {
        this.resistanceSlash = resistanceSlash;
    }

    public Resistance getResistancePierce() {
        return resistancePierce;
    }

    public void setResistancePierce(Resistance resistancePierce) {
        this.resistancePierce = resistancePierce;
    }

    public Resistance getResistanceBlunt() {
        return resistanceBlunt;
    }

    public void setResistanceBlunt(Resistance resistanceBlunt) {
        this.resistanceBlunt = resistanceBlunt;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }
}
