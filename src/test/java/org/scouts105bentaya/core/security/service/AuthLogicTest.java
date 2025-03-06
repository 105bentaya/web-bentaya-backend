package org.scouts105bentaya.core.security.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.entity.BookingDocument;
import org.scouts105bentaya.features.booking.enums.BookingStatus;
import org.scouts105bentaya.features.event.Event;
import org.scouts105bentaya.features.event.dto.EventFormDto;
import org.scouts105bentaya.features.event.service.EventService;
import org.scouts105bentaya.features.pre_scout.entity.PreScout;
import org.scouts105bentaya.features.pre_scout.entity.PreScoutAssignation;
import org.scouts105bentaya.features.pre_scout.service.PreScoutService;
import org.scouts105bentaya.features.scout.Scout;
import org.scouts105bentaya.features.scout.ScoutService;
import org.scouts105bentaya.features.user.User;
import org.scouts105bentaya.shared.service.AuthService;
import org.scouts105bentaya.utils.GroupUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class AuthLogicTest {

    @Mock
    AuthService authService;
    @Mock
    ScoutService scoutService;
    @Mock
    EventService eventService;
    @Mock
    PreScoutService preScoutService;


    private AuthLogic authLogic;

    @BeforeEach
    void setUp() {
        authLogic = new AuthLogic(authService, scoutService, eventService, preScoutService);
    }

    @Test
    void userHasSameGroupIdAsScoutShouldReturnTrue() {
        //given
        var scout = new Scout().setId(1).setGroup(GroupUtils.basicGroup());
        var loggedUser = new User().setId(1).setGroup(GroupUtils.basicGroup());

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(scoutService.findById(1)).thenReturn(scout);
        var result = authLogic.userHasSameGroupIdAsScout(1);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void userHasDifferentGroupIdAsScoutShouldReturnFalse() {
        //given
        var scout = new Scout().setId(1).setGroup(GroupUtils.groupOfId(2));
        var loggedUser = new User().setId(1).setGroup(GroupUtils.basicGroup());

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(scoutService.findById(1)).thenReturn(scout);
        var result = authLogic.userHasSameGroupIdAsScout(1);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void userHasNoGroupIdShouldReturnFalse() {
        //given
        var scout = new Scout().setId(1).setGroup(GroupUtils.groupOfId(2));
        var loggedUser = new User().setId(1);

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.verifyNoInteractions(scoutService);
        var result = authLogic.userHasSameGroupIdAsScout(1);

        //then
        Assertions.assertThat(result).isFalse();
    }

    void mockScout() {
        Mockito.when(scoutService.findById(1)).thenReturn(new Scout().setId(1).setGroup(GroupUtils.basicGroup()));
    }

    @Test
    void eventIsEditableByScouterWhenIdIsCorrect() {
        //given
        var loggedUser = new User().setId(1).setGroup(GroupUtils.basicGroup());
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
        var loggedUser = new User().setId(1).setGroup(GroupUtils.groupOfId(2));
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
        var loggedUser = new User().setId(1).setGroup(GroupUtils.basicGroup());
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
        var loggedUser = new User().setId(1).setGroup(GroupUtils.basicGroup());
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
        var loggedUser = new User().setId(1);
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
        var loggedUser = new User().setId(1).setGroup(GroupUtils.basicGroup());
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
        var loggedUser = new User().setId(1).setGroup(GroupUtils.basicGroup());
        var eventDB = new Event().setId(1).setGroup(GroupUtils.groupOfId(2));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(eventService.findById(1)).thenReturn(eventDB);
        var result = authLogic.scouterHasAccessToEvent(1);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void userHasGroupId() {
        //given
        var loggedUser = new User().setId(1).setGroup(GroupUtils.basicGroup());

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        var result = authLogic.userHasGroupId(1);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void userHasGroupId2() {
        //given
        var loggedUser = new User().setId(1).setGroup(GroupUtils.basicGroup());

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        var result = authLogic.userHasGroupId(2);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void userHasGroupId3() {
        //given
        var loggedUser = new User().setId(1);

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        var result = authLogic.userHasGroupId(2);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void preScoutHasGroupId() {
        //given
        var preScout = new PreScout().setId(1).setPreScoutAssignation(new PreScoutAssignation().setGroup(GroupUtils.basicGroup()));

        //when
        Mockito.when(preScoutService.findById(1)).thenReturn(preScout);
        var result = authLogic.preScoutHasGroupId(1, 1);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void preScoutHasNotGroupId() {
        //given
        var preScout = new PreScout().setId(1).setPreScoutAssignation(new PreScoutAssignation().setGroup(GroupUtils.basicGroup()));

        //when
        Mockito.when(preScoutService.findById(1)).thenReturn(preScout);
        var result = authLogic.preScoutHasGroupId(1, 2);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void userHasPreScoutGroupId() {
        //given
        var loggedUser = new User().setId(1).setGroup(GroupUtils.basicGroup());
        var preScout = new PreScout().setId(1).setPreScoutAssignation(new PreScoutAssignation().setGroup(GroupUtils.basicGroup()));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(preScoutService.findById(1)).thenReturn(preScout);
        var result = authLogic.userHasPreScoutGroupId(1);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void userHasPreScoutGroupId2() {
        //given
        var loggedUser = new User().setId(1);
        var preScout = new PreScout().setId(1).setPreScoutAssignation(new PreScoutAssignation().setGroup(GroupUtils.basicGroup()));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(preScoutService.findById(1)).thenReturn(preScout);
        var result = authLogic.userHasPreScoutGroupId(1);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void userHasPreScoutGroupId3() {
        //given
        var loggedUser = new User().setId(1).setGroup(GroupUtils.groupOfId(2));
        var preScout = new PreScout().setId(1).setPreScoutAssignation(new PreScoutAssignation().setGroup(GroupUtils.basicGroup()));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(preScoutService.findById(1)).thenReturn(preScout);
        var result = authLogic.userHasPreScoutGroupId(1);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void userHasScoutId() {
        //given
        var loggedUser = new User().setId(1).setScoutList(Set.of(new Scout().setId(1), new Scout().setId(2)));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        var result = authLogic.userHasScoutId(1);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void userHasScoutId2() {
        //given
        var loggedUser = new User().setId(1).setScoutList(Set.of(new Scout().setId(1), new Scout().setId(2)));

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
        var booking = new Booking().setId(1).setUser(loggedUser);
        loggedUser.setBookingList(List.of(booking));

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
        var booking = new Booking().setId(1).setUser(loggedUser).setBookingDocumentList(List.of(new BookingDocument().setId(1)));
        loggedUser.setBookingList(List.of(booking));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        var result = authLogic.userOwnsBookingDocument(1);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void userOwnsBookingDocument2() {
        //given
        var loggedUser = new User().setId(1);
        var booking = new Booking().setId(1).setUser(loggedUser).setBookingDocumentList(List.of(new BookingDocument().setId(2)));
        loggedUser.setBookingList(List.of(booking));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        var result = authLogic.userOwnsBookingDocument(1);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void userOwnsBookingDocument3() {
        //given
        var loggedUser = new User().setId(1).setBookingList(Collections.emptyList());

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        var result = authLogic.userOwnsBookingDocument(1);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void userCanEditBookingDocument() {
        //given
        var loggedUser = new User().setId(1);
        var bookingDocument = new BookingDocument().setId(1);
        var booking = new Booking().setId(1).setUser(loggedUser).setStatus(BookingStatus.RESERVED)
            .setBookingDocumentList(List.of(bookingDocument));
        bookingDocument.setBooking(booking);
        loggedUser.setBookingList(List.of(booking));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        var result = authLogic.userCanEditBookingDocument(1);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void userCanEditBookingDocument2() {
        //given
        var loggedUser = new User().setId(1);
        var bookingDocument = new BookingDocument().setId(1);
        var booking = new Booking().setId(1).setUser(loggedUser).setStatus(BookingStatus.OCCUPIED)
            .setBookingDocumentList(List.of(bookingDocument));
        bookingDocument.setBooking(booking);
        loggedUser.setBookingList(List.of(booking));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        var result = authLogic.userCanEditBookingDocument(1);

        //then
        Assertions.assertThat(result).isFalse();
    }
    @Test
    void userCanEditBookingDocument3() {
        //given
        var loggedUser = new User().setId(1);
        var booking = new Booking().setId(1).setUser(loggedUser).setStatus(BookingStatus.RESERVED)
            .setBookingDocumentList(List.of(new BookingDocument().setId(2)));
        loggedUser.setBookingList(List.of(booking));

        //when
        Mockito.when(authService.getLoggedUser()).thenReturn(loggedUser);
        var result = authLogic.userCanEditBookingDocument(1);

        //then
        Assertions.assertThat(result).isFalse();
    }

}