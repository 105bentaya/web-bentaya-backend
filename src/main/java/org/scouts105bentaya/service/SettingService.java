package org.scouts105bentaya.service;

import org.scouts105bentaya.entity.Setting;

import java.util.List;

public interface SettingService {
    List<Setting> findAll();

    Setting findByName(String name);

    Setting updateValue(String value, String name);
}
