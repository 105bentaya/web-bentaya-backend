package org.scouts105bentaya.features.scout.converter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scouts105bentaya.features.group.GroupBasicDataDto;
import org.scouts105bentaya.features.group.GroupService;
import org.scouts105bentaya.features.scout.OldScout;
import org.scouts105bentaya.features.scout.dto.ScoutDto;
import org.scouts105bentaya.features.scout_contact.Contact;
import org.scouts105bentaya.features.scout_contact.ContactConverter;
import org.scouts105bentaya.features.scout_contact.ContactDto;
import org.scouts105bentaya.utils.GroupUtils;

import java.time.Instant;
import java.util.Date;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class ScoutConverterTest {
    private ScoutConverter scoutConverter;

    @Mock
    private GroupService groupService;

    @Mock
    private ContactConverter contactConverter;

    @BeforeEach
    void setUp() {
        scoutConverter = new ScoutConverter(contactConverter, groupService);
    }

    @Test
    void scoutConverterDoesNotReturnUnmodifiableList() {
        OldScout scout = scoutConverter.convertFromDto(buildScoutDto());
        scout.getContactList().add(new Contact());
        Assertions.assertThat(scout.getContactList()).hasSize(2);
    }

    private OldScout buildScout() {
        Contact contact = new Contact();
        contact.setName("asdads");
        contact.setPhone("asdasddas");
        contact.setRelationship("asdasdas");
        contact.setId(23);
        OldScout scout = new OldScout();
        scout.setId(1);
        scout.setGroup(GroupUtils.basicGroup());
        scout.setName("OldScout 1");
        scout.setSurname("OldScout 1");
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
            GroupBasicDataDto.fromGroup(GroupUtils.basicGroup()),
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