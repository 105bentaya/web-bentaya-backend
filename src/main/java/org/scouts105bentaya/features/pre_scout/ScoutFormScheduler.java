package org.scouts105bentaya.features.pre_scout;

import org.scouts105bentaya.features.setting.SettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ScoutFormScheduler {

    private static final Logger log = LoggerFactory.getLogger(ScoutFormScheduler.class);
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

    @Scheduled(cron = "0 0 0 16 AUG ?", zone = "Atlantic/Canary")
    private void closeScoutForm() {
        log.info("Closing scout form");
        settingService.updateValue("0", "formIsOpen");
    }

}
