package org.scouts105bentaya.features.scout;

import org.apache.commons.lang3.StringUtils;
import org.scouts105bentaya.features.group.Group;
import org.scouts105bentaya.features.group.Section;
import org.scouts105bentaya.features.scout.dto.form.IdDocumentFormDto;
import org.scouts105bentaya.features.scout.entity.IdentificationDocument;
import org.scouts105bentaya.features.scout.entity.Scout;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

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

    public static IdentificationDocument updateIdDocument(IdentificationDocument identificationDocument, IdDocumentFormDto idForm) {
        if (idForm == null) {
            return null;
        }
        if (identificationDocument != null) {
            identificationDocument.setNumber(idForm.number());
            identificationDocument.setIdType(idForm.idType());
            return identificationDocument;
        } else {
            return new IdentificationDocument()
                .setNumber(idForm.number())
                .setIdType(idForm.idType());
        }
    }

    public static String getScoutGroupSection(Scout scout) {
        Optional<Group> optionalGroup = Optional.ofNullable(scout.getGroup());
        return switch (scout.getScoutType()) {
            case SCOUT -> optionalGroup.map(group -> titleCase(group.getSection().name())).orElse(null);
            case SCOUTER -> titleCase(Section.SCOUTERS.name());
            case COMMITTEE, MANAGER -> titleCase(Section.SCOUTSUPPORT.name());
            case INACTIVE -> "Sin Sección - Baja";
        };
    }

    private static String titleCase(String string) {
        return StringUtils.capitalize(string.toLowerCase());
    }

    public static String getScoutGroupName(Scout scout) {
        Optional<Group> optionalGroup = Optional.ofNullable(scout.getGroup());
        return switch (scout.getScoutType()) {
            case SCOUT -> optionalGroup.map(Group::getName).orElse(null);
            case SCOUTER -> optionalGroup.map(group -> "Kraal - " + group.getName()).orElse("Kraal");
            case COMMITTEE -> "Almogaren";
            case MANAGER -> "Tagoror";
            case INACTIVE -> "Guatatiboa";
        };
    }
}
