package org.scouts105bentaya.core.security.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scouts105bentaya.features.booking.entity.BookingDocument;
import org.scouts105bentaya.features.booking.entity.BookingDocumentFile;
import org.scouts105bentaya.features.booking.entity.GeneralBooking;
import org.scouts105bentaya.features.booking.enums.BookingDocumentStatus;
import org.scouts105bentaya.features.booking.enums.BookingStatus;
import org.scouts105bentaya.features.booking.repository.BookingDocumentRepository;
import org.scouts105bentaya.features.booking.repository.OwnBookingRepository;
import org.scouts105bentaya.features.event.Event;
import org.scouts105bentaya.features.event.dto.EventFormDto;
import org.scouts105bentaya.features.event.service.EventService;
import org.scouts105bentaya.features.group.Group;
import org.scouts105bentaya.features.pre_scout.entity.PreScout;
import org.scouts105bentaya.features.pre_scout.entity.PreScoutAssignation;
import org.scouts105bentaya.features.pre_scout.service.PreScoutService;
import org.scouts105bentaya.features.scout.dto.form.NewScoutFormDto;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.entity.ScoutFile;
import org.scouts105bentaya.features.scout.enums.ScoutFileType;
import org.scouts105bentaya.features.scout.enums.ScoutType;
import org.scouts105bentaya.features.scout.repository.ScoutFileRepository;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.features.user.User;
import org.scouts105bentaya.features.user.role.RoleEnum;
import org.scouts105bentaya.shared.service.AuthService;
import org.scouts105bentaya.utils.GroupUtils;
import org.scouts105bentaya.utils.RoleUtils;
import org.scouts105bentaya.utils.ScoutUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class AuthLogicTest {

    @Mock
    AuthService authService;
    @Mock
    EventService eventService;
    @Mock
    PreScoutService preScoutService;
    @Mock
    BookingDocumentRepository bookingDocumentRepository;
    @Mock
    OwnBookingRepository ownBookingRepository;
    @Mock
    ScoutRepository scoutRepository;
    @Mock
    ScoutFileRepository scoutFileRepository;


    private AuthLogic authLogic;

    @BeforeEach
    void setUp() {
        authLogic = new AuthLogic(authService, eventService, preScoutService, bookingDocumentRepository, ownBookingRepository, scoutRepository, scoutFileRepository);
    }

    @Test
    void userHasAccessToScoutShouldReturnTrue() {
        //given
        var scout = ScoutUtils.scoutOfId(1);
        var loggedUser = new User().setId(1).setScouter(scout).setScoutList(Set.of(scout)).setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_USER)));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        var result = authLogic.userHasAccessToScout(1);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void userHasAccessToScoutShouldReturnFalse1() {
        //given
        var scout = ScoutUtils.scoutOfId(1);

        var loggedUser = new User().setId(1).setScouter(scout).setScoutList(Set.of(scout)).setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_SCOUTER)));
        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        var result = authLogic.userHasAccessToScout(1);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void userHasAccessScoutShouldReturnFalse() {
        //given
        var scout = ScoutUtils.scoutOfId(1);
        var loggedUser = new User().setId(2).setScouter(scout).setScoutList(Set.of(scout)).setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_SCOUTER)));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        var result = authLogic.userHasAccessToScout(1);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void userHasAccessScoutShouldReturnFalse2() {
        //given
        var loggedUser = new User().setId(1).setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_USER)));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        var result = authLogic.userHasAccessToScout(1);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void eventIsEditableByScouterWhenIdIsCorrect() {
        //given
        var loggedUser = new User().setId(1).setScouter(ScoutUtils.basicScouter());
        var eventFormDto = EventFormDto.builder().id(1).groupId(1).build();
        var eventDB = new Event().setId(1).setGroup(GroupUtils.basicGroup());

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(eventService.findById(1)).thenReturn(eventDB);
        var result = authLogic.eventIsEditableByScouter(eventFormDto);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void eventIsEditableByScouter2() {
        //given
        var loggedUser = new User().setId(1).setScouter(ScoutUtils.basicScouter(GroupUtils.groupOfId(2)));
        var eventFormDto = EventFormDto.builder().id(1).groupId(1).build();
        var eventDB = new Event().setId(1).setGroup(GroupUtils.basicGroup());

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(eventService.findById(1)).thenReturn(eventDB);
        var result = authLogic.eventIsEditableByScouter(eventFormDto);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void eventIsEditableByScouter3() {
        //given
        var loggedUser = new User().setId(1).setScouter(ScoutUtils.basicScouter());
        var eventFormDto = EventFormDto.builder().id(1).groupId(2).build();
        var eventDB = new Event().setId(1).setGroup(GroupUtils.basicGroup());

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(eventService.findById(1)).thenReturn(eventDB);
        var result = authLogic.eventIsEditableByScouter(eventFormDto);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void eventIsEditableByScouter4() {
        //given
        var loggedUser = new User().setId(1).setScouter(ScoutUtils.basicScouter());
        var eventFormDto = EventFormDto.builder().id(1).groupId(1).build();
        var eventDB = new Event().setId(1).setGroup(GroupUtils.groupOfId(2));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(eventService.findById(1)).thenReturn(eventDB);
        var result = authLogic.eventIsEditableByScouter(eventFormDto);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void eventIsEditableByScouter5() {
        //given
        var loggedUser = new User().setId(1);
        var eventFormDto = EventFormDto.builder().id(1).forEveryone(true).build();
        var eventDB = new Event().setId(1).setForEveryone(true);

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(eventService.findById(1)).thenReturn(eventDB);
        var result = authLogic.eventIsEditableByScouter(eventFormDto);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void eventIsEditableByScouter6() {
        //given
        var loggedUser = new User().setId(1);
        var eventFormDto = EventFormDto.builder().id(1).forEveryone(true).build();
        var eventDB = new Event().setId(1).setForEveryone(false).setGroup(GroupUtils.basicGroup());

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(eventService.findById(1)).thenReturn(eventDB);
        var result = authLogic.eventIsEditableByScouter(eventFormDto);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void eventIsEditableByScouter7() {
        //given
        var loggedUser = new User().setId(1);
        var eventFormDto = EventFormDto.builder().id(1).forEveryone(false).groupId(1).build();
        var eventDB = new Event().setId(1).setForEveryone(true);

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(eventService.findById(1)).thenReturn(eventDB);
        var result = authLogic.eventIsEditableByScouter(eventFormDto);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void scouterHasAccessToEvent() {
        //given
        var eventDB = new Event().setId(1).setForEveryone(true);

        //when
        Mockito.when(eventService.findById(1)).thenReturn(eventDB);
        var result = authLogic.scouterHasAccessToEvent(1);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void scouterHasAccessToEvent2() {
        //given
        var loggedUser = new User().setId(1);
        var eventDB = new Event().setId(1).setGroup(GroupUtils.basicGroup());

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(eventService.findById(1)).thenReturn(eventDB);
        var result = authLogic.scouterHasAccessToEvent(1);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void scouterHasAccessToEvent3() {
        //given
        var loggedUser = new User().setId(1).setScouter(ScoutUtils.basicScouter());
        var eventDB = new Event().setId(1).setGroup(GroupUtils.basicGroup());

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(eventService.findById(1)).thenReturn(eventDB);
        var result = authLogic.scouterHasAccessToEvent(1);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void scouterHasAccessToEvent4() {
        //given
        var loggedUser = new User().setId(1).setScouter(ScoutUtils.basicScouter());
        var eventDB = new Event().setId(1).setGroup(GroupUtils.groupOfId(2));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(eventService.findById(1)).thenReturn(eventDB);
        var result = authLogic.scouterHasAccessToEvent(1);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void scouterHasGroupId() {
        //given
        var loggedUser = new User().setId(1).setScouter(ScoutUtils.basicScouter());

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        var result = authLogic.scouterHasGroupId(1);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void scouterHasGroupId2() {
        //given
        var loggedUser = new User().setId(1).setScouter(ScoutUtils.basicScouter());

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        var result = authLogic.scouterHasGroupId(2);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void scouterHasGroupId3() {
        //given
        var loggedUser = new User().setId(1);

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        var result = authLogic.scouterHasGroupId(2);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void scouterHasPreScoutGroupId() {
        //given
        var loggedUser = new User().setId(1).setScouter(ScoutUtils.basicScouter());
        var preScout = new PreScout().setId(1).setPreScoutAssignation(new PreScoutAssignation().setGroup(GroupUtils.basicGroup()));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(preScoutService.findById(1)).thenReturn(preScout);
        var result = authLogic.scouterHasPreScoutGroupId(1);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void scouterHasPreScoutGroupId2() {
        //given
        var loggedUser = new User().setId(1);
        var preScout = new PreScout().setId(1).setPreScoutAssignation(new PreScoutAssignation().setGroup(GroupUtils.basicGroup()));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(preScoutService.findById(1)).thenReturn(preScout);
        var result = authLogic.scouterHasPreScoutGroupId(1);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void scouterHasPreScoutGroupId3() {
        //given
        var loggedUser = new User().setId(1).setScouter(ScoutUtils.basicScouter(GroupUtils.groupOfId(2)));
        var preScout = new PreScout().setId(1).setPreScoutAssignation(new PreScoutAssignation().setGroup(GroupUtils.basicGroup()));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(preScoutService.findById(1)).thenReturn(preScout);
        var result = authLogic.scouterHasPreScoutGroupId(1);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void userHasScoutId() {
        //given
        var loggedUser = new User().setId(1).setScoutList(Set.of(ScoutUtils.scoutOfId(1), ScoutUtils.scoutOfId(2)));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        var result = authLogic.userHasScoutId(1);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void userHasScoutId2() {
        //given
        var loggedUser = new User().setId(1).setScoutList(Set.of(ScoutUtils.scoutOfId(1), ScoutUtils.scoutOfId(2)));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        var result = authLogic.userHasScoutId(3);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void userHasScoutId3() {
        //given
        var loggedUser = new User().setId(1).setScoutList(Collections.emptySet());

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        var result = authLogic.userHasScoutId(1);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void userOwnsBooking() {
        //given
        var loggedUser = new User().setId(1);
        var booking = new GeneralBooking().setUser(loggedUser).setId(1);
        loggedUser.setBookingList(List.of((GeneralBooking) booking));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        var result = authLogic.userOwnsBooking(1);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void userOwnsBooking2() {
        //given
        var loggedUser = new User().setId(1).setBookingList(Collections.emptyList());

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        var result = authLogic.userOwnsBooking(1);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void userOwnsBookingDocument() {
        //given
        var loggedUser = new User().setId(1);
        var doc = new BookingDocument().setId(1).setFile(new BookingDocumentFile().setUser(loggedUser));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(bookingDocumentRepository.get(1)).thenReturn(doc);
        var result = authLogic.userOwnsBookingDocumentFile(1);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void userOwnsBookingDocument2() {
        //given
        var loggedUser = new User().setId(1);
        var doc = new BookingDocument().setId(1).setFile(new BookingDocumentFile().setUser(new User().setId(2)));
        var booking = new GeneralBooking().setUser(loggedUser).setBookingDocumentList(List.of(doc)).setId(1);
        loggedUser.setBookingList(List.of((GeneralBooking) booking));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(bookingDocumentRepository.get(1)).thenReturn(doc);
        var result = authLogic.userOwnsBookingDocumentFile(1);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void userOwnsBookingDocument3() {
        //given
        var loggedUser = new User().setId(1);
        var doc = new BookingDocument().setId(2);
        var booking = new GeneralBooking().setUser(loggedUser).setBookingDocumentList(List.of(doc)).setId(1);
        loggedUser.setBookingList(List.of((GeneralBooking) booking));
        doc.setFile(new BookingDocumentFile().setUser(loggedUser));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(bookingDocumentRepository.get(1)).thenReturn(new BookingDocument().setId(1).setFile(new BookingDocumentFile().setUser(new User().setId(3))));
        var result = authLogic.userOwnsBookingDocumentFile(1);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void userOwnsBookingDocument4() {
        //given
        var loggedUser = new User().setId(1).setBookingList(Collections.emptyList());

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(bookingDocumentRepository.get(1)).thenReturn(new BookingDocument().setId(1).setFile(new BookingDocumentFile().setUser(new User().setId(4))));
        var result = authLogic.userOwnsBookingDocumentFile(1);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void userCanEditBookingDocument() {
        //given
        var loggedUser = new User().setId(1);
        var bookingDocument = new BookingDocument().setId(1).setStatus(BookingDocumentStatus.REJECTED);
        var booking = new GeneralBooking().setUser(loggedUser).setBookingDocumentList(List.of(bookingDocument)).setId(1).setStatus(BookingStatus.RESERVED);
        bookingDocument.setBooking((GeneralBooking) booking);
        loggedUser.setBookingList(List.of((GeneralBooking) booking));
        bookingDocument.setFile(new BookingDocumentFile().setUser(loggedUser));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(bookingDocumentRepository.get(1)).thenReturn(bookingDocument);
        var result = authLogic.userCanEditBookingDocumentFile(1);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void userCanEditBookingDocument2() {
        //given
        var loggedUser = new User().setId(1);
        var bookingDocument = new BookingDocument().setId(1).setStatus(BookingDocumentStatus.ACCEPTED);
        var booking = new GeneralBooking().setUser(loggedUser).setBookingDocumentList(List.of(bookingDocument))
            .setId(1).setStatus(BookingStatus.RESERVED);
        bookingDocument.setBooking((GeneralBooking) booking);
        loggedUser.setBookingList(List.of((GeneralBooking) booking));
        bookingDocument.setFile(new BookingDocumentFile().setUser(loggedUser));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(bookingDocumentRepository.get(1)).thenReturn(bookingDocument);
        var result = authLogic.userCanEditBookingDocumentFile(1);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void userCanEditBookingDocument3() {
        //given
        var loggedUser = new User().setId(1);
        var bookingDocument = new BookingDocument().setId(1);
        var booking = new GeneralBooking().setUser(loggedUser).setBookingDocumentList(List.of(bookingDocument))
            .setStatus(BookingStatus.OCCUPIED).setId(1);
        bookingDocument.setBooking((GeneralBooking) booking);
        loggedUser.setBookingList(List.of((GeneralBooking) booking));
        bookingDocument.setFile(new BookingDocumentFile().setUser(loggedUser));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(bookingDocumentRepository.get(1)).thenReturn(bookingDocument);
        var result = authLogic.userCanEditBookingDocumentFile(1);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void userCanEditBookingDocument4() {
        //given
        var loggedUser = new User().setId(1);
        var booking = new GeneralBooking().setUser(loggedUser).setBookingDocumentList(List.of(new BookingDocument().setId(2)))
            .setStatus(BookingStatus.RESERVED).setId(1);
        loggedUser.setBookingList(List.of((GeneralBooking) booking));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(bookingDocumentRepository.get(1)).thenReturn(new BookingDocument().setFile(new BookingDocumentFile().setUser(new User().setId(3))));
        var result = authLogic.userCanEditBookingDocumentFile(1);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void userHasAccessToScout_withUserRoleAndInList_returnsTrue() {
        //given
        Scout scout = ScoutUtils.scoutOfId(1);
        User user = new User()
            .setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_USER)))
            .setScoutList(Set.of(scout));

        Mockito.when(authService.getLoggedUser()).thenReturn(user);
        boolean canAccess = authLogic.userHasAccessToScout(1);

        //then
        Assertions.assertThat(canAccess).isTrue();
    }

    @Test
    void userHasAccessToScout_withUserRoleAndNotInList_returnsFalse() {
        //given
        Scout scout = ScoutUtils.scoutOfId(2);
        User user = new User()
            .setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_USER)))
            .setScoutList(Set.of(scout));

        Mockito.when(authService.getLoggedUser()).thenReturn(user);
        boolean canAccess = authLogic.userHasAccessToScout(1);

        //then
        Assertions.assertThat(canAccess).isFalse();
    }

    @Test
    void userHasAccessToScout_withoutUserRole_returnsFalse() {
        //given
        Scout scout = ScoutUtils.scoutOfId(1);
        User user = new User()
            .setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_SCOUTER)))
            .setScoutList(Set.of(scout));

        Mockito.when(authService.getLoggedUser()).thenReturn(user);
        boolean canAccess = authLogic.userHasAccessToScout(1);

        //then
        Assertions.assertThat(canAccess).isFalse();
    }

    @Test
    void userHasAccessToScout_notInScoutList_returnsFalse() {
        //given
        User user = new User()
            .setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_USER)))
            .setScoutList(Set.of());

        Mockito.when(authService.getLoggedUser()).thenReturn(user);
        boolean canAccess = authLogic.userHasAccessToScout(1);

        //then
        Assertions.assertThat(canAccess).isFalse();
    }

    @Test
    void isUserWithAccessToScoutFile_validFileAndInDocuments_returnsTrue() {
        //given
        int fileId = 100;
        ScoutFile file = new ScoutFile();
        file.setId(fileId);
        Mockito.when(scoutFileRepository.findById(fileId)).thenReturn(Optional.of(file));

        Scout scout = ScoutUtils.scoutOfId(1);
        scout.getMedicalData().getDocuments().add(file);
        User user = new User()
            .setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_USER)))
            .setScoutList(Set.of(scout));

        Mockito.when(authService.getLoggedUser()).thenReturn(user);

        //when
        boolean hasAccess = authLogic.isUserWithAccessToScoutFile(100);

        //then
        Assertions.assertThat(hasAccess).isTrue();
    }

    @Test
    void isUserWithAccessToScoutFile_validFileAndNotInDocuments_returnsFalse() {
        //given
        int fileId = 100;
        ScoutFile file = new ScoutFile();
        file.setId(fileId);

        int notFileId = 200;
        ScoutFile file2 = new ScoutFile();
        file2.setId(fileId);
        Mockito.when(scoutFileRepository.findById(notFileId)).thenReturn(Optional.of(file2));

        Scout scout = ScoutUtils.scoutOfId(1);
        scout.getMedicalData().getDocuments().add(file);
        User user = new User()
            .setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_USER)))
            .setScoutList(Set.of(scout));

        Mockito.when(authService.getLoggedUser()).thenReturn(user);

        //when
        boolean hasAccess = authLogic.isUserWithAccessToScoutFile(200);

        //then
        Assertions.assertThat(hasAccess).isFalse();
    }

    @Test
    void isUserWithAccessToScoutFile_fileNotFound_returnsFalse() {
        //given
        int fileId = 200;
        Mockito.when(scoutFileRepository.findById(fileId)).thenReturn(Optional.empty());
        User user = new User()
            .setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_USER)))
            .setScoutList(Set.of());
        Mockito.when(authService.getLoggedUser()).thenReturn(user);

        //when
        boolean hasAccess = authLogic.isUserWithAccessToScoutFile(200);

        //then
        Assertions.assertThat(hasAccess).isFalse();
    }

    @Test
    void isUserWithAccessToScoutFile_withoutUserRole_returnsFalse() {
        //given
        User user = new User()
            .setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_SCOUTER)));
        Mockito.when(authService.getLoggedUser()).thenReturn(user);

        //when
        boolean hasAccess = authLogic.isUserWithAccessToScoutFile(300);

        //then
        Assertions.assertThat(hasAccess).isFalse();
    }

    @Test
    void isUserWithAccessToScoutFile_fileNotInAnyDocuments_returnsFalse() {
        //given
        int fileId = 400;
        ScoutFile file = new ScoutFile();
        file.setId(fileId);
        Mockito.when(scoutFileRepository.findById(fileId)).thenReturn(Optional.of(file));

        Scout scout = ScoutUtils.scoutOfId(1);
        User user = new User()
            .setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_USER)))
            .setScoutList(Set.of(scout));
        Mockito.when(authService.getLoggedUser()).thenReturn(user);

        //when
        boolean hasAccess = authLogic.isUserWithAccessToScoutFile(400);

        //then
        Assertions.assertThat(hasAccess).isFalse();
    }

    @Test
    void isScouterAndCanEditScout_asOwner_returnsTrue() {
        //given
        int scoutId = 10;
        Scout owner = ScoutUtils.scoutOfId(scoutId);
        User user = new User()
            .setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_SCOUTER)))
            .setScouter(owner);
        Mockito.when(authService.getLoggedUser()).thenReturn(user);

        //when
        var result = authLogic.isScouterAndCanEditScout(scoutId);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void isScouterAndCanEditScout_inSameGroup_returnsTrue() {
        //given
        int scoutId = 20;
        Group group = GroupUtils.basicGroup();
        User user = new User()
            .setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_SCOUTER)))
            .setScouter(ScoutUtils.basicScouter(group));
        Mockito.when(authService.getLoggedUser()).thenReturn(user);
        Mockito.when(scoutRepository.findScoutGroup(scoutId)).thenReturn(Optional.of(group));

        //then
        Assertions.assertThat(authLogic.isScouterAndCanEditScout(scoutId)).isTrue();
    }

    @Test
    void isScouterAndCanEditScout_notInSameGroup_returnsFalse() {
        //given
        int scoutId = 20;
        User user = new User()
            .setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_SCOUTER)))
            .setScouter(ScoutUtils.basicScouter(GroupUtils.groupOfId(2)));
        Mockito.when(authService.getLoggedUser()).thenReturn(user);
        Mockito.when(scoutRepository.findScoutGroup(scoutId)).thenReturn(Optional.of(GroupUtils.basicGroup()));

        //then
        Assertions.assertThat(authLogic.isScouterAndCanEditScout(scoutId)).isFalse();
    }

    @Test
    void isScouterAndCanEditScout_withoutScouterRole_returnsFalse() {
        //given
        User user = new User()
            .setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_USER)));
        Mockito.when(authService.getLoggedUser()).thenReturn(user);

        //then
        Assertions.assertThat(authLogic.isScouterAndCanEditScout(1)).isFalse();
    }

    @Test
    void isScouterAndCanEditGroupScout_inSameGroup_returnsTrue() {
        //given
        int scoutId = 50;
        Group group = GroupUtils.groupOfId(8);
        User user = new User()
            .setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_SCOUTER)))
            .setScouter(ScoutUtils.basicScouter(group));
        Mockito.when(authService.getLoggedUser()).thenReturn(user);
        Mockito.when(scoutRepository.findScoutGroup(scoutId)).thenReturn(Optional.of(group));

        //then
        Assertions.assertThat(authLogic.isScouterAndCanEditGroupScout(scoutId)).isTrue();
    }

    @Test
    void isScouterAndCanEditGroupScout_notInGroup_returnsFalse() {
        //given
        int scoutId = 60;
        User user = new User()
            .setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_SCOUTER)))
            .setScouter(ScoutUtils.scoutOfId(scoutId));
        Mockito.when(authService.getLoggedUser()).thenReturn(user);
        Mockito.when(scoutRepository.findScoutGroup(scoutId)).thenReturn(Optional.of(GroupUtils.groupOfId(2)));

        //then
        Assertions.assertThat(authLogic.isScouterAndCanEditGroupScout(scoutId)).isFalse();
    }

    @Test
    void isScouterAndCanUploadDocument_recordType_returnsFalse() {
        Assertions.assertThat(authLogic.isScouterAndCanUploadDocument(1, ScoutFileType.RECORD)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = ScoutFileType.class, mode = EnumSource.Mode.EXCLUDE, names = {"RECORD"})
    void isScouterAndCanUploadDocument_asOwnerValidType_returnsTrue(ScoutFileType scoutFileType) {
        //given
        int id = 70;
        Scout owner = ScoutUtils.scoutOfId(id);
        User user = new User()
            .setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_SCOUTER)))
            .setScouter(owner);
        Mockito.when(authService.getLoggedUser()).thenReturn(user);

        //then
        Assertions.assertThat(authLogic.isScouterAndCanUploadDocument(id, scoutFileType)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = ScoutFileType.class, mode = EnumSource.Mode.EXCLUDE, names = {"RECORD"})
    void isScouterAndCanUploadDocument_notScouter_returnsFalse(ScoutFileType scoutFileType) {
        //given
        User user = new User()
            .setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_USER)));
        Mockito.when(authService.getLoggedUser()).thenReturn(user);

        //then
        Assertions.assertThat(authLogic.isScouterAndCanUploadDocument(1, scoutFileType)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = ScoutType.class, names = {"SCOUT", "SCOUTER"})
    void isScouterAndCanAddScout_validData_returnsTrue(ScoutType scoutType) {
        //given
        NewScoutFormDto dto = Mockito.mock(NewScoutFormDto.class);
        Mockito.when(dto.scoutType()).thenReturn(scoutType);
        Mockito.when(dto.census()).thenReturn(null);

        User user = new User()
            .setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_SCOUTER)));
        Mockito.when(authService.getLoggedUser()).thenReturn(user);

        //then
        Assertions.assertThat(authLogic.isScouterAndCanAddScout(dto)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = ScoutType.class, names = {"SCOUT", "SCOUTER"})
    void isScouterAndCanAddScout_withCensus_returnsFalse(ScoutType scoutType) {
        //given
        NewScoutFormDto dto = Mockito.mock(NewScoutFormDto.class);
        Mockito.when(dto.scoutType()).thenReturn(scoutType);
        Mockito.when(dto.census()).thenReturn(1234);

        User user = new User()
            .setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_SCOUTER)));
        Mockito.when(authService.getLoggedUser()).thenReturn(user);

        //then
        Assertions.assertThat(authLogic.isScouterAndCanAddScout(dto)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = RoleEnum.class, mode = EnumSource.Mode.EXCLUDE, names = {"ROLE_SCOUTER"})
    void isScouterAndCanAddScout_notScouterRole_returnsFalse(RoleEnum role) {
        //given
        NewScoutFormDto dto = Mockito.mock(NewScoutFormDto.class);

        User user = new User()
            .setRoles(List.of(RoleUtils.of(role)));
        Mockito.when(authService.getLoggedUser()).thenReturn(user);

        //then
        Assertions.assertThat(authLogic.isScouterAndCanAddScout(dto)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = ScoutType.class, mode = EnumSource.Mode.EXCLUDE, names = {"SCOUT", "SCOUTER"})
    void isScouterAndCanAddScout_invalidType_returnsFalse(ScoutType scoutType) {
        //given
        NewScoutFormDto dto = Mockito.mock(NewScoutFormDto.class);
        Mockito.when(dto.scoutType()).thenReturn(scoutType);
        User user = new User()
            .setRoles(List.of(RoleUtils.of(RoleEnum.ROLE_SCOUTER)));
        Mockito.when(authService.getLoggedUser()).thenReturn(user);

        //then
        Assertions.assertThat(authLogic.isScouterAndCanAddScout(dto)).isFalse();
    }
}
