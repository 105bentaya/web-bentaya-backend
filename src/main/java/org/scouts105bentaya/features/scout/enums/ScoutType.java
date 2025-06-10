package org.scouts105bentaya.features.scout.enums;

public enum ScoutType {
    SCOUT, SCOUTER, COMMITTEE, MANAGER, INACTIVE;

    public boolean isScoutOrScouter() {
        return this == ScoutType.SCOUT || this == ScoutType.SCOUTER;
    }
}
