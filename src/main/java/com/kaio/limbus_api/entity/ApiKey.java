package com.kaio.limbus_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 12)
    private String prefixo;

    @Column(nullable = false, unique = true, length = 64)
    private String hash;

    @Column(nullable = false)
    private boolean ativa = true;

    @Column(nullable = false)
    private LocalDateTime criadaEm;

    private LocalDateTime ultimoUso;

    public ApiKey() {
    }

    public ApiKey(String nome, String email, String prefixo, String hash) {
        this.nome = nome;
        this.email = email;
        this.prefixo = prefixo;
        this.hash = hash;
        this.criadaEm = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getPrefixo() {
        return prefixo;
    }

    public String getHash() {
        return hash;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public LocalDateTime getCriadaEm() {
        return criadaEm;
    }

    public LocalDateTime getUltimoUso() {
        return ultimoUso;
    }

    public void setUltimoUso(LocalDateTime ultimoUso) {
        this.ultimoUso = ultimoUso;
    }
}
