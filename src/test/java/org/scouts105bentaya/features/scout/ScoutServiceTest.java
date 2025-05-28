package org.scouts105bentaya.features.scout;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scouts105bentaya.features.confirmation.service.ConfirmationService;
import org.scouts105bentaya.features.group.GroupBasicDataDto;
import org.scouts105bentaya.features.group.GroupService;
import org.scouts105bentaya.features.scout.converter.ScoutConverter;
import org.scouts105bentaya.features.scout.dto.OldScoutDto;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.features.scout.service.ScoutService;
import org.scouts105bentaya.features.scout_contact.Contact;
import org.scouts105bentaya.features.scout_contact.ContactConverter;
import org.scouts105bentaya.features.scout_contact.ContactDto;
import org.scouts105bentaya.features.scout_contact.ContactRepository;
import org.scouts105bentaya.utils.GroupUtils;

import java.time.Instant;
import java.util.Date;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class ScoutServiceTest {

    @InjectMocks
    private ScoutService scoutService;

    @Mock
    private ScoutRepository scoutRepository;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private ConfirmationService confirmationService;

    @Mock
    private GroupService groupService;

    private ScoutConverter scoutConverter;

    @BeforeEach
    void setUp() {
//        MockitoAnnotations.openMocks(this);
        scoutConverter = new ScoutConverter(new ContactConverter(), groupService);  // No mocking, using the actual converter
        scoutService = new ScoutService(scoutRepository, contactRepository, scoutConverter, null, null, null, confirmationService, null);
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

    private OldScoutDto buildScoutDto() {
        return new OldScoutDto(
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