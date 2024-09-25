package org.scouts105bentaya.service.impl;

import org.scouts105bentaya.entity.Setting;
import org.scouts105bentaya.exception.SettingNotFoundException;
import org.scouts105bentaya.repository.SettingsRepository;
import org.scouts105bentaya.service.SettingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingServiceImpl implements SettingService {

    private final SettingsRepository settingsRepository;

    public SettingServiceImpl(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Override
    public List<Setting> findAll() {
        return settingsRepository.findAll();
    }

    @Override
    public Setting findByName(String name) {
        return this.settingsRepository.findByName(name).orElseThrow(SettingNotFoundException::new);
    }

    @Override
    public Setting updateValue(String value, String name) {

        Setting settingToUpdate = this.findByName(name);
        settingToUpdate.setValue(value);

        return this.settingsRepository.save(settingToUpdate);
    }
}
