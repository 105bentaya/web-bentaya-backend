package org.scouts105bentaya.features.setting;

import org.scouts105bentaya.core.exception.SettingNotFoundException;
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

    public Setting findByName(String name) {
        return this.settingsRepository.findByName(name).orElseThrow(SettingNotFoundException::new);
    }

    public Setting updateValue(String value, String name) {
        Setting settingToUpdate = this.findByName(name);
        settingToUpdate.setValue(value);

        return this.settingsRepository.save(settingToUpdate);
    }
}
