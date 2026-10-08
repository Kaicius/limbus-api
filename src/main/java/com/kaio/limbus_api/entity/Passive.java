package com.kaio.limbus_api.entity;

import com.kaio.limbus_api.enums.PassivaType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Schema(description = "Passiva de uma Identity, de combate (BATTLE) ou suporte (SUPPORT)")
public class Passive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único, gerado pelo banco", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Schema(description = "Tipo da Passiva: BATTLE (combate) ou SUPPORT (suporte)", example = "BATTLE")
    private PassivaType tipo;

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Nome da Passiva", example = "The Eye of Precognition", maxLength = 255)
    private String nome;

    @NotBlank
    @Column(length = 2000)
    @Size(max = 2000)
    @Schema(description = "Descrição do efeito da Passiva", example = "[Turn Start] Gain Prescript...", maxLength = 2000)
    private String descricao;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "identity_id")
    @Schema(description = "Identity dona da Passiva (relação N:1, definida pela URL)")
    private Identity identity;

    public Passive() {
    }

    public Passive(PassivaType tipo, String nome, String descricao,
                   Identity identity) {
        this.tipo = tipo;
        this.nome = nome;
        this.descricao = descricao;
        this.identity = identity;
    }

    public long getId() {
        return id;
    }

    public PassivaType getTipo() {
        return tipo;
    }

    public void setTipo(PassivaType tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }
}
