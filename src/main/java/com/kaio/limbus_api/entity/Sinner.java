package com.kaio.limbus_api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Schema(description = "Um dos 12 Sinners da Limbus Company")
public class Sinner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único, gerado pelo banco", example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(unique = true)
    @Schema(description = "Nome do Sinner (único)", example = "Yi Sang", minLength = 2, maxLength = 100)
    @Pattern(regexp = "^(?=.*\\p{L}).+$", message = "deve conter letras (não pode ser só números ou símbolos)")
    private String nome;

    public Sinner() {
    }

    public Sinner(String nome) {
        this.nome = nome;
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
}
