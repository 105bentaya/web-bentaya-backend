package org.scouts105bentaya.features.pre_scout;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.setting.SettingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class ScoutFormScheduler {

    private final SettingService settingService;

    public ScoutFormScheduler(SettingService settingService) {
        this.settingService = settingService;
    }

    @Scheduled(cron = "0 0 0 10 JAN ?", zone = "Atlantic/Canary")
    private void openScoutForm() {
        log.info("Opening scout form");
        LocalDate date = LocalDate.now();
        settingService.updateValue("1", "formIsOpen");
        settingService.updateValue(String.valueOf(date.getYear() + 1), "currentFormYear");
    }

    @Scheduled(cron = "0 0 0 1 AUG ?", zone = "Atlantic/Canary")
    private void closeScoutForm() {
        log.info("Closing scout form");
        settingService.updateValue("0", "formIsOpen");
    }

}
