package org.scouts105bentaya.features.jamboree_inscription.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class JamboreeForm {
    @NotNull
    @Length(max = 255)
    private String participantType;
    @NotNull
    @Length(max = 255)
    private String surname;
    @NotNull
    @Length(max = 255)
    private String name;
    @Length(max = 255)
    private String feltName;
    @NotNull
    @Length(max = 255)
    private String dni;
    @NotNull
    @Length(max = 255)
    private String passportNumber;
    @NotNull
    @Length(max = 255)
    private String nationality;
    @NotNull
    private LocalDate birthDate;
    @Length(max = 255)
    @NotNull
    private String gender;
    @Length(max = 255)
    @NotNull
    private String phoneNumber;
    @Length(max = 255)
    @NotNull
    private String email;

    @Length(max = 255)
    @NotNull
    private String bloodType;
    @Length(max = 2000)
    @NotNull
    private String medicalData;
    @NotNull
    @Length(max = 2000)
    private String medication;
    @NotNull
    @Length(max = 2000)
    private String allergies;
    @NotNull
    private boolean vaccineProgram;
    @NotNull
    private boolean resident;
    @Length(max = 255)
    private String municipality;
    @NotNull
    @Length(max = 511)
    private String address;
    @NotNull
    @Length(max = 255)
    private String cp;
    @NotNull
    @Length(max = 255)
    private String locality;



    @NotNull
    @Length(max = 255)
    private String size;
    @NotNull
    @Length(max = 2000)
    private String foodIntolerances;
    @Length(max = 2000)
    private String dietPreference;
    @Length(max = 2000)
    private String observations;

    @NotEmpty
    @Valid
    private List<JamboreeFormLanguage> languages;

    @Valid
    private JamboreeFormContact mainContact;

    private JamboreeFormContact secondaryContact;

    @Getter
    @Setter
    public static class JamboreeFormLanguage {
        @NotNull
        @Length(max = 255)
        private String language;
        @NotNull
        @Length(max = 255)
        private String level;
    }

    @Getter
    @Setter
    public static class JamboreeFormContact {
        @NotNull
        @Length(max = 255)
        private String surname;
        @NotNull
        @Length(max = 255)
        private String name;
        @NotNull
        @Length(max = 255)
        private String mobilePhone;
        @NotNull
        @Length(max = 255)
        private String email;
        @Length(max = 255)
        private String landlinePhone;
    }
}
