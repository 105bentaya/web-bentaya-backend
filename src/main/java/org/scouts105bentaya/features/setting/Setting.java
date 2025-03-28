package org.scouts105bentaya.features.setting;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.features.setting.enums.SettingEnum;
import org.scouts105bentaya.features.setting.enums.SettingType;

@Getter
@Setter
@Entity
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar", unique = true, nullable = false)
    private SettingEnum name;
    @NotNull
    @Column(nullable = false)
    private String value;
}
