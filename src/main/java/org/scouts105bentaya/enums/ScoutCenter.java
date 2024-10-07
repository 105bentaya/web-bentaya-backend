package org.scouts105bentaya.enums;

import lombok.Getter;

@Getter
public enum ScoutCenter {
    TERRENO("Campamento Bentaya", 45, 30, "Normas Campamento Bentaya.pdf"),
    TEJEDA("Refugio Tejeda", 20, 10, "Normas Refugio Tejeda.pdf"),
    REFUGIO_TERRENO("Refugio Luis Martín", 20, 10, "Normas Refugio Luis Martín.pdf"),
    PALMITAL("Aula de la Naturaleza El Palmital", 20, 10, "Normas Palmital.pdf");

    private final String name;
    private final int maxCapacity;
    private final int exclusiveReservationCapacity;
    private final String pdfName;

    ScoutCenter(String name, int maxCapacity, int exclusiveReservationCapacity, String pdfName) {
        this.name = name;
        this.maxCapacity = maxCapacity;
        this.exclusiveReservationCapacity = exclusiveReservationCapacity;
        this.pdfName = pdfName;
    }
}