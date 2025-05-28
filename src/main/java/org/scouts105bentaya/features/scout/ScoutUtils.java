package org.scouts105bentaya.features.scout;

import org.scouts105bentaya.features.scout.dto.form.IdDocumentFormDto;
import org.scouts105bentaya.features.scout.entity.IdentificationDocument;
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

}
