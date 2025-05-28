package org.scouts105bentaya.features.scout;

import org.scouts105bentaya.features.scout.entity.Scout;

import java.time.LocalDate;
import java.time.Period;

public class ScoutUtils {

    private ScoutUtils() {
    }

    public static String getScoutSection(Scout scout) {
        int age = Period.between(scout.getPersonalData().getBirthday().plusDays(1), LocalDate.now()).getYears();

        if (age >= 30) {
            return "Sénior";
        }
        if (age >= 14) {
            return "Juvenil";
        }
        return "Infantil";
    }
}
