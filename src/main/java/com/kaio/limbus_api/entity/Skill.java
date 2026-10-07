package com.kaio.limbus_api.entity;

import com.kaio.limbus_api.enums.Sin;
import com.kaio.limbus_api.enums.SkillSlot;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SkillSlot slot;

    @Min(1)
    private int variante;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Sin sin;

    @NotBlank
    @Size(max = 255)
    private String nome;

    @Min(1)
    private int quantidadeCoins;

    @NotBlank
    @Column(length = 2000)
    @Size(max = 2000)
    private String descricaoEfeito;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "identity_id")
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
