package org.scouts105bentaya.features.pre_scout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PreScoutUtils {

    private PreScoutUtils() {
    }

    public static String getGroup(String date, int firstYearOfTerm) {
        int scoutBirthYear = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy")).getYear();
        int yearDifference = firstYearOfTerm - scoutBirthYear;

        return switch (yearDifference) {
            case 6, 7 -> "CASTOR";
            case 8, 9, 10 -> "LOBATO";
            case 11, 12, 13 -> "SCOUT";
            case 14, 15, 16 -> "ESCULTA";
            case 17, 18, 19, 20 -> "ROVER";
            default -> "NINGÚN GRUPO";
        };
    }

    public static String getPriority(int priorityGroup, int inscriptionYear) {
        int previousYear = inscriptionYear - 1;
        return switch (priorityGroup) {
            case 1 -> String.format(
                "1. Tiene hermanas/os o es hija/o de scouters que están en el grupo desde al menos la Ronda Solar %d/%d",
                previousYear - 1, previousYear - 2000
            );
            case 2 -> "2. Es hija/o de scouters o scouts que hayan pertenecido al grupo";
            default -> "Ninguno";
        };
    }

    public static boolean hasPriority(int priorityGroup) {
        return priorityGroup == 1 || priorityGroup == 2;
    }
}
