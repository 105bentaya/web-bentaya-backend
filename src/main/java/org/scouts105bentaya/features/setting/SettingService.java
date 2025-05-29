package org.scouts105bentaya.features.setting;

import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.setting.dto.SettingInDto;
import org.scouts105bentaya.features.setting.dto.SettingOutDto;
import org.scouts105bentaya.features.setting.enums.SettingEnum;
import org.scouts105bentaya.features.setting.enums.SettingType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingService {

    private final SettingsRepository settingsRepository;

    public SettingService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public List<Setting> findAll() {
        return settingsRepository.findAll();
    }

    public Setting findByName(SettingEnum name) {
        return this.settingsRepository.findByName(name).orElseThrow(WebBentayaNotFoundException::new);
    }

    public void updateValue(Object value, SettingEnum name) {
        updateValue(new SettingInDto(value), name);
    }

    public SettingOutDto updateValue(SettingInDto settingInDto, SettingEnum name) {
        Setting settingToUpdate = this.findByName(name);
        SettingEnum setting = settingToUpdate.getName();
        Object value = settingInDto.settingValue();
        if (value == null || value == "" || value == "null") {
            if (setting.isNullable()) {
                settingToUpdate.setValue("");
            } else {
                throw new WebBentayaBadRequestException("El ajuste %s no puede ser nulo".formatted(name));
            }
        } else if (setting.getType() == SettingType.BOOLEAN) {
            settingToUpdate.setValue((boolean) value ? "1" : "0");
        } else {
            settingToUpdate.setValue(value.toString());
        }

        return SettingOutDto.fromSetting(this.settingsRepository.save(settingToUpdate));
    }
}
