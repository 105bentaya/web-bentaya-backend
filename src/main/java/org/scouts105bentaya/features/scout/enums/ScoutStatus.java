package org.scouts105bentaya.features.scout.enums;

public enum ScoutStatus {
    ACTIVE, PENDING_NEW, PENDING_EXISTING, INACTIVE;

    public String getStringValue() {
        return switch (this) {
            case INACTIVE -> "Baja";
            case ACTIVE -> "Alta";
            default -> "Alta Pendiente";
        };
    }
}
