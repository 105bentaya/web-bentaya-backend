package org.scouts105bentaya.features.scout.enums;

public enum ScoutType {
    SCOUT, SCOUTER, COMMITTEE, MANAGER, INACTIVE;

    public boolean isScoutOrScouter() {
        return this == SCOUT || this == SCOUTER;
    }

    public boolean isScouterOrScoutSupport() {
        return this == COMMITTEE || this == MANAGER || this == SCOUTER;
    }
}
