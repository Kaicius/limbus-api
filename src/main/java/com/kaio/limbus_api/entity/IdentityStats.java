package com.kaio.limbus_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
public class IdentityStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Min(1)
    private int hp;

    @Min(1)
    private int speed;

    @Min(0)
    private int defense;

    @Min(1)
    private int staggerThreshold;

    @PositiveOrZero
    private double resistanceSlash;
    @PositiveOrZero
    private double resistancePierce;
    @PositiveOrZero
    private double resistanceBlunt;

    @NotNull
    @OneToOne
    @JoinColumn(name = "identity_id", unique = true)
    private Identity identity;

    public IdentityStats() {
    }

    public IdentityStats(int hp, int speed, int defense, int staggerThreshold,
                         double resistanceSlash, double resistancePierce, double resistanceBlunt,
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

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
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

    public double getResistanceSlash() {
        return resistanceSlash;
    }

    public void setResistanceSlash(double resistanceSlash) {
        this.resistanceSlash = resistanceSlash;
    }

    public double getResistancePierce() {
        return resistancePierce;
    }

    public void setResistancePierce(double resistancePierce) {
        this.resistancePierce = resistancePierce;
    }

    public double getResistanceBlunt() {
        return resistanceBlunt;
    }

    public void setResistanceBlunt(double resistanceBlunt) {
        this.resistanceBlunt = resistanceBlunt;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }
}
