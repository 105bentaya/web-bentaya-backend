package org.scouts105bentaya.shared.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FormUtils {

    private FormUtils() {
    }

    public static String getGroup(String date, boolean fullName, int firstYearOfTerm) {
        int scoutBirthYear = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy")).getYear();

        int yearDifference = firstYearOfTerm - scoutBirthYear;
        if (yearDifference > 20 || yearDifference < 6) return fullName ? "NINGÚN GRUPO" : "NaG";
        if (yearDifference >= 17) return fullName ? "ROVER" : "ROV";
        if (yearDifference >= 14) return fullName ? "ESCULTA" : "ESC";
        if (yearDifference >= 11) return fullName ? "SCOUT" : "SCT";
        if (yearDifference >= 8) return fullName ? "LOBATO" : "LOB";
        return fullName ? "CASTOR" : "CAS";
    }

    public static String getPriority(int priorityGroup, int year) {
        return switch (priorityGroup) {
            case 1 -> String.format(
                "1. Tiene hermanos/as o es hija de scouters que están en el grupo en la Ronda Solar %d/%d",
                year - 1, year - 2000
            );
            case 2 -> "2. Es hija de scouters o scouts que hayan pertenecido al grupo, a SEC o a ASDE";
            case 3 -> "3. Tiene hermanos o hermanas en la lista de espera para la misma ronda";
            case 4 -> "Ninguno";
            default -> "Juanete";
        };
    }

    public static boolean hasPriority(int priorityGroup) {
        return priorityGroup == 1 || priorityGroup == 2 || priorityGroup == 3;
    }

    //todo check if it can be replaced by utf encoding
    public static String removeSpecialCharacters(String str) {
        return str.replaceAll("[, ]+", "_");
    }
}
