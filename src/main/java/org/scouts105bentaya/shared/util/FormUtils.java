package org.scouts105bentaya.shared.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FormUtils {

    private FormUtils() {
    }

    public static String getGroup(String date, boolean fullName, int firstYearOfTerm) {
        int scoutBirthYear = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy")).getYear();
        int yearDifference = firstYearOfTerm - scoutBirthYear;

        return switch (yearDifference) {
            case 6, 7 -> fullName ? "CASTOR" : "CAS";
            case 8, 9, 10 -> fullName ? "LOBATO" : "LOB";
            case 11, 12, 13 -> fullName ? "SCOUT" : "SCT";
            case 14, 15, 16 -> fullName ? "ESCULTA" : "ESC";
            case 17, 18, 19, 20 -> fullName ? "ROVER" : "ROV";
            default -> fullName ? "NINGÚN GRUPO" : "NaG";
        };
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
