package org.scouts105bentaya.features.scout.converter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.scouts105bentaya.features.scout.Scout;
import org.scouts105bentaya.features.scout.dto.ScoutDto;
import org.scouts105bentaya.features.scout_contact.Contact;
import org.scouts105bentaya.features.scout_contact.ContactConverter;
import org.scouts105bentaya.features.scout_contact.ContactDto;
import org.scouts105bentaya.shared.Group;

import java.time.Instant;
import java.util.Date;
import java.util.stream.Stream;

class ScoutConverterTest {

    private ScoutConverter scoutConverter;

    @BeforeEach
    void setUp() {
        scoutConverter = new ScoutConverter(new ContactConverter());
    }

    @Test
    void scoutConverterDoesNotReturnUnmodifiableList() {
        Scout scout = scoutConverter.convertFromDto(buildScoutDto());
        scout.getContactList().add(new Contact());
        Assertions.assertThat(scout.getContactList()).hasSize(2);
    }

    private Scout buildScout() {
        Contact contact = new Contact();
        contact.setName("asdads");
        contact.setPhone("asdasddas");
        contact.setRelationship("asdasdas");
        contact.setId(23);
        Scout scout = new Scout();
        scout.setId(1);
        scout.setGroupId(Group.GARAJONAY);
        scout.setName("Scout 1");
        scout.setSurname("Scout 1");
        scout.setDni("dni");
        scout.setBirthday(Date.from(Instant.now()));
        scout.setMedicalData("Medical data");
        scout.setGender("M");
        scout.setImageAuthorization(false);
        scout.setShirtSize("S");
        scout.setMunicipality("c");
        scout.setCensus(2);
        scout.setProgressions("");
        scout.setObservations("");
        scout.setContactList(Stream.of(contact).toList());
        scout.setEnabled(true);
        return scout;
    }

    private ScoutDto buildScoutDto() {
        return new ScoutDto(
            1,
            1,
            "",
            "",
            "",
            Date.from(Instant.now()),
            "",
            "",
            false,
            "",
            "",
            2,
            "",
            "",
            Stream.of(new ContactDto(1, "Pepe", "Parent", "12313", "asdd@asddas")).toList(),
            true,
            false);
    }
}