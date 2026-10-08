package com.kaio.limbus_api.entity;

import com.kaio.limbus_api.enums.Rarity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Schema(description = "Versão jogável de um Sinner, com raridade, uptie e Tags")
public class Identity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único, gerado pelo banco", example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @NotBlank
    @Size(max = 100)
    @Column(unique = true)
    @Schema(description = "Nome da Identity (único)", example = "[The House of Spiders: The Index Nursefather] Yi Sang", maxLength = 100)
    private String nome;

    @Min(1)
    @Max(4)
    @Schema(description = "Quantos uptie tem (1 a 4)", example = "4", minimum = "1", maximum = "4")
    private int uptie;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Rarity rarity;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "sinner_id")
    private Sinner sinner;

    @NotEmpty
    @ManyToMany
    @JoinTable(
            name = "identity_tag",
            joinColumns = @JoinColumn(name = "identity_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

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

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}
