package org.scouts105bentaya.features.setting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingsRepository extends JpaRepository<Setting, Integer> {
    Optional<Setting> findByName(SettingEnum name);
}
