package org.scouts105bentaya.features.setting;

import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
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

    public Setting updateValue(String value, SettingEnum name) {
        Setting settingToUpdate = this.findByName(name);
        settingToUpdate.setValue(value);

        return this.settingsRepository.save(settingToUpdate);
    }
}
