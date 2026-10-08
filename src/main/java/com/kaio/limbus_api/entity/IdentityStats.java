package com.kaio.limbus_api.entity;

import com.kaio.limbus_api.enums.Resistance;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Schema(description = "Atributos de combate de uma Identity (HP, velocidade, defesa, Stagger e resistências)")
public class IdentityStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Min(1)
    private int hp;

    @NotBlank
    @Pattern(regexp = "^[1-9]\\d*-[1-9]\\d*$", message = "deve estar no formato mín-máx, ex.: 3-7")
    private String speed;

    @Min(0)
    private int defense;

    @Min(1)
    private int staggerThreshold;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Resistance resistanceSlash;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Resistance resistancePierce;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Resistance resistanceBlunt;

    @NotNull
    @OneToOne
    @JoinColumn(name = "identity_id", unique = true)
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
