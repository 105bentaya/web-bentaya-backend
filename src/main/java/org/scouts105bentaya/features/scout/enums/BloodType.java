package org.scouts105bentaya.features.scout.enums;

public enum BloodType {
    O_POSITIVE, O_NEGATIVE, A_POSITIVE, A_NEGATIVE, B_POSITIVE, B_NEGATIVE, AB_POSITIVE, AB_NEGATIVE, NA;

    public String getStringValue() {
        return switch (this) {
            case NA -> "Sin Especificar";
            case O_POSITIVE -> "O+";
            case A_POSITIVE -> "A+";
            case B_POSITIVE -> "B+";
            case AB_POSITIVE -> "AB+";
            case O_NEGATIVE -> "O-";
            case A_NEGATIVE -> "A-";
            case B_NEGATIVE -> "B-";
            case AB_NEGATIVE -> "AB-";
        };
    }
}
