package com.kaio.limbus_api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Schema(description = "Sanity de uma Identity: Panic e fatores que aumentam ou diminuem a Sanity")
public class Sanity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Size(max = 2000)
    @Column(length = 2000)
    private String panicType;

    @NotBlank
    @Size(max = 2000)
    @Column(length = 2000)
    private String increasingFactors;

    @NotBlank
    @Size(max = 2000)
    @Column(length = 2000)
    private String decreasingFactors;

    @NotNull
    @OneToOne
    @JoinColumn(name = "identity_id", unique = true)
    private Identity identity;

    public Sanity() {
    }

    public Sanity(String panicType, String increasingFactors,
                  String decreasingFactors, Identity identity) {
        this.panicType = panicType;
        this.increasingFactors = increasingFactors;
        this.decreasingFactors = decreasingFactors;
        this.identity = identity;
    }

    public long getId() {
        return id;
    }

    public String getPanicType() {
        return panicType;
    }

    public void setPanicType(String panicType) {
        this.panicType = panicType;
    }

    public String getIncreasingFactors() {
        return increasingFactors;
    }

    public void setIncreasingFactors(String increasingFactors) {
        this.increasingFactors = increasingFactors;
    }

    public String getDecreasingFactors() {
        return decreasingFactors;
    }

    public void setDecreasingFactors(String decreasingFactors) {
        this.decreasingFactors = decreasingFactors;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }
}
