package org.scouts105bentaya.features.scout;

import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scouts105bentaya.features.confirmation.service.ConfirmationService;
import org.scouts105bentaya.features.scout.converter.ScoutConverter;
import org.scouts105bentaya.features.scout.dto.ScoutDto;
import org.scouts105bentaya.features.scout_contact.Contact;
import org.scouts105bentaya.features.scout_contact.ContactConverter;
import org.scouts105bentaya.features.scout_contact.ContactDto;
import org.scouts105bentaya.features.scout_contact.ContactRepository;
import org.scouts105bentaya.shared.Group;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;

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

    private ScoutConverter scoutConverter;

    @BeforeEach
    void setUp() {
//        MockitoAnnotations.openMocks(this);
        scoutConverter = new ScoutConverter(new ContactConverter());  // No mocking, using the actual converter
        scoutService = new ScoutService(scoutRepository, contactRepository, scoutConverter, null, null, null, confirmationService, null);
    }

    @Test
    void update() {
        Mockito.when(scoutRepository.findByIdAndEnabledIsTrue(1)).thenReturn(Optional.of(buildScout()));
        Mockito.when(scoutRepository.save(any(Scout.class))).thenReturn(buildScout());

        Scout updatedScout = scoutService.update(buildScoutDto());

        updatedScout.getContactList().add(new Contact());
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