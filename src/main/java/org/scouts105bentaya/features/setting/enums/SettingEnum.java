package org.scouts105bentaya.features.setting.enums;

import lombok.Getter;

@Getter
public enum SettingEnum {
    CURRENT_FORM_YEAR(SettingType.NUMBER),
    FORM_IS_OPEN(SettingType.BOOLEAN),
    CURRENT_YEAR(SettingType.NUMBER),
    MAINTENANCE(SettingType.DATE, true),
    BOOKING_MAIL(SettingType.STRING),
    CONTACT_MAIL(SettingType.STRING),
    COMPLAINT_MAIL(SettingType.STRING),
    FORM_MAIL(SettingType.STRING),
    TREASURY_MAIL(SettingType.STRING),
    ADMINISTRATION_MAIL(SettingType.STRING),
    BOOKING_DATE(SettingType.DATE),
    BOOKING_MIN_DAY_NUMBER(SettingType.NUMBER),
    BOOKING_MAX_DAY_NUMBER(SettingType.NUMBER);

    private final SettingType type;
    private final boolean nullable;

    SettingEnum(SettingType type, boolean nullable) {
        this.type = type;
        this.nullable = nullable;
    }

    SettingEnum(SettingType type) {
        this.type = type;
        this.nullable = false;
    }

    public boolean isBookingRelated() {
        return this == BOOKING_DATE || this == BOOKING_MIN_DAY_NUMBER || this == BOOKING_MAX_DAY_NUMBER;
    }
}
