package com.kaio.limbus_api.entity;

import com.kaio.limbus_api.enums.Rarity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Identity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String nome;

    @Size(min = 1, max = 4)
    private int uptie;

    @Enumerated(EnumType.STRING)
    private Rarity rarity;

    @ManyToOne
    @JoinColumn(name = "sinner_id")
    private Sinner sinner;

    public Identity() {
    }

    public Identity(String nome, int uptie, Rarity rarity, Sinner sinner) {
        this.nome = nome;
        this.uptie = uptie;
        this.rarity = rarity;
        this.sinner = sinner;
    }

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getUptie() {
        return uptie;
    }

    public void setUptie(int uptie) {
        this.uptie = uptie;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    public Sinner getSinner() {
        return sinner;
    }

    public void setSinner(Sinner sinner) {
        this.sinner = sinner;
    }
}
