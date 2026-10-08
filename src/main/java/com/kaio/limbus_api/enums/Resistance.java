package com.kaio.limbus_api.enums;


public enum Resistance {
    INEFFECTIVE(0.5),
    NORMAL(1.0),
    WEAK(1.5),
    FATAL(2.0);

    private final double multiplicador;

    Resistance(double multiplicador) {
        this.multiplicador = multiplicador;
    }

    public double getMultiplicador() {
        return multiplicador;
    }
}
