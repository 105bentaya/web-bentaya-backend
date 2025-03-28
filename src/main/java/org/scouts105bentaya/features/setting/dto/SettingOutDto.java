package org.scouts105bentaya.features.setting.dto;

import org.scouts105bentaya.features.setting.Setting;
import org.scouts105bentaya.features.setting.enums.SettingEnum;
import org.scouts105bentaya.features.setting.enums.SettingType;

public record SettingOutDto(
    SettingEnum name, Object value, SettingType type
) {
    public static SettingOutDto fromSetting(Setting setting) {
        return new SettingOutDto(setting.getName(), setting.getValue(), setting.getName().getType());
    }
}
