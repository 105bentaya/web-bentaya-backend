package org.scouts105bentaya.features.event.service;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.confirmation.Confirmation;
import org.scouts105bentaya.features.confirmation.service.ConfirmationService;
import org.scouts105bentaya.features.event.Event;
import org.scouts105bentaya.features.event.EventRepository;
import org.scouts105bentaya.features.event.dto.EventFormDto;
import org.scouts105bentaya.features.scout.Scout;
import org.scouts105bentaya.features.scout.ScoutService;
import org.scouts105bentaya.shared.Group;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    static ZonedDateTime start = ZonedDateTime.parse("2025-03-12T10:00:00Z");
    static ZonedDateTime end = ZonedDateTime.parse("2025-03-12T12:00:00Z");


    @InjectMocks
    EventService eventService;

    @Mock
    private ScoutService scoutService;

    @Mock
    ConfirmationService confirmationService;

    @Mock
    EventRepository eventRepository;

    @Test
    void onSaveEventAttendance_NoDates() {
        //given
        var eventFormDto = EventFormDto.builder().groupId(1).build();

        //when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> eventService.save(eventFormDto);

        //then
        Assertions.assertThatThrownBy(throwingCallable)
            .isInstanceOf(WebBentayaBadRequestException.class)
            .hasMessageStartingWith("Fechas no especificadas");
    }

    @Test
    void onSaveEventAttendance_BadDates() {
        //given
        var eventFormDto = EventFormDto.builder().groupId(1).unknownTime(true).startDate(start).endDate(end).build();

        //when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> eventService.save(eventFormDto);

        //then
        Assertions.assertThatThrownBy(throwingCallable)
            .isInstanceOf(WebBentayaBadRequestException.class)
            .hasMessageStartingWith("Fechas no especificadas");
    }

    @Test
    void onSaveEventAttendance_BadDates2() {
        //given
        var eventFormDto = EventFormDto.builder().groupId(1).unknownTime(false).localStartDate(start.toLocalDate()).localEndDate(end.toLocalDate()).build();

        //when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> eventService.save(eventFormDto);

        //then
        Assertions.assertThatThrownBy(throwingCallable)
            .isInstanceOf(WebBentayaBadRequestException.class)
            .hasMessageStartingWith("Fechas no especificadas");
    }

    @Test
    void onSaveEventAttendance_BadDates3() {
        //given
        var eventFormDto = EventFormDto.builder().groupId(1).startDate(end).endDate(start).build();

        //when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> eventService.save(eventFormDto);

        //then
        Assertions.assertThatThrownBy(throwingCallable)
            .isInstanceOf(WebBentayaBadRequestException.class)
            .hasMessageStartingWith("La fecha de fin no debe ser anterior a la de inicio");
    }

    @Test
    void onSave_AttendanceIsCreated() {
        //given
        var eventFormDto = EventFormDto.builder().groupId(1).activateAttendanceList(true).startDate(start).endDate(end).build();

        //when
        when(scoutService.findAllByLoggedScouterGroupId()).thenReturn(List.of(new Scout().setId(1)));
        ZonedDateTime date1 = ZonedDateTime.parse("2025-03-13T11:00:00Z");
        ZonedDateTime date2 = ZonedDateTime.parse("2025-03-12T11:00:00Z");
        mockSave();
        Event savedEvent = eventService.save(eventFormDto);

        //then
        Mockito.verify(confirmationService, Mockito.times(1)).save(any(Confirmation.class));
        Assertions.assertThat(savedEvent).isNotNull();

        try (MockedStatic<ZonedDateTime> mockedLocalDateTime = Mockito.mockStatic(ZonedDateTime.class)) {
            mockedLocalDateTime.when(ZonedDateTime::now).thenReturn(date1);
            Assertions.assertThat(savedEvent.eventAttendanceIsClosed()).isTrue();
            mockedLocalDateTime.when(ZonedDateTime::now).thenReturn(date2);
            Assertions.assertThat(savedEvent.eventAttendanceIsClosed()).isFalse();
        }
    }

    @Test
    void onSaveWithClosedDate_AttendanceIsCreated() {
        //given
        var eventFormDto = EventFormDto.builder().groupId(1)
            .activateAttendanceList(true).startDate(start).endDate(end).closeDateTime(ZonedDateTime.parse("2025-03-10T11:00:00Z"))
            .build();

        //when
        when(scoutService.findAllByLoggedScouterGroupId()).thenReturn(List.of(new Scout().setId(1)));
        ZonedDateTime date1 = ZonedDateTime.parse("2025-03-10T12:00:00Z");
        ZonedDateTime date2 = ZonedDateTime.parse("2025-03-09T11:00:00Z");
        mockSave();
        Event savedEvent = eventService.save(eventFormDto);

        //then
        Mockito.verify(confirmationService, Mockito.times(1)).save(any(Confirmation.class));
        Assertions.assertThat(savedEvent).isNotNull();

        try (MockedStatic<ZonedDateTime> mockedLocalDateTime = Mockito.mockStatic(ZonedDateTime.class)) {
            mockedLocalDateTime.when(ZonedDateTime::now).thenReturn(date1);
            Assertions.assertThat(savedEvent.eventAttendanceIsClosed()).isTrue();
            mockedLocalDateTime.when(ZonedDateTime::now).thenReturn(date2);
            Assertions.assertThat(savedEvent.eventAttendanceIsClosed()).isFalse();
        }
    }

    @Test
    void onSaveWithClosedNow_AttendanceIsCreated() {
        //given
        var eventFormDto = EventFormDto.builder().groupId(1)
            .activateAttendanceList(true).startDate(start).endDate(end)
            .closeDateTime(ZonedDateTime.parse("2025-03-10T11:00:00Z")).closeAttendanceList(true)
            .build();

        //when
        when(scoutService.findAllByLoggedScouterGroupId()).thenReturn(List.of(new Scout().setId(1)));
        ZonedDateTime date1 = ZonedDateTime.parse("2025-03-10T12:00:00Z");
        ZonedDateTime date2 = ZonedDateTime.parse("2025-03-09T11:00:00Z");
        mockSave();
        Event savedEvent = eventService.save(eventFormDto);

        //then
        Mockito.verify(confirmationService, Mockito.times(1)).save(any(Confirmation.class));
        Assertions.assertThat(savedEvent).isNotNull();

        try (MockedStatic<ZonedDateTime> mockedLocalDateTime = Mockito.mockStatic(ZonedDateTime.class)) {
            mockedLocalDateTime.when(ZonedDateTime::now).thenReturn(date1);
            Assertions.assertThat(savedEvent.eventAttendanceIsClosed()).isTrue();
            mockedLocalDateTime.when(ZonedDateTime::now).thenReturn(date2);
            Assertions.assertThat(savedEvent.eventAttendanceIsClosed()).isTrue();
        }
    }

    @Test
    void onUpdateThrowErrorWhenEventDoesNotExists() {
        //given
        var eventFormDto = buildEventFormDto();

        //when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> eventService.update(eventFormDto);

        //then
        Assertions.assertThatThrownBy(throwingCallable)
            .isInstanceOf(WebBentayaNotFoundException.class);
    }

    @Test
    void onUpdateEventAttendance_NoDates() {
        //given
        var eventFormDto = EventFormDto.builder().id(1).build();
        var event = new Event().setId(1);

        //when
        when(eventRepository.findById(anyInt())).thenReturn(Optional.of(event));
        ThrowableAssert.ThrowingCallable throwingCallable = () -> eventService.update(eventFormDto);

        //then
        Assertions.assertThatThrownBy(throwingCallable)
            .isInstanceOf(WebBentayaBadRequestException.class)
            .hasMessageStartingWith("Fechas no especificadas");
    }

    @Test
    void onUpdateEventAttendance_AttendanceIsCreated() {
        //given
        ZonedDateTime lateEnd = ZonedDateTime.parse("2025-03-14T12:00:00Z");

        var eventFormDto = EventFormDto.builder().id(1).groupId(1).activateAttendanceList(true)
            .startDate(start).endDate(lateEnd).build();
        var event = new Event().setId(1).setGroupId(Group.GARAJONAY).setActiveAttendanceList(false);

        //when
        when(eventRepository.findById(anyInt())).thenReturn(Optional.of(event));
        when(scoutService.findAllByLoggedScouterGroupId()).thenReturn(List.of(new Scout().setId(1)));
        ZonedDateTime mockedNow = ZonedDateTime.parse("2025-03-13T12:00:00Z");
        mockSave();
        Event updatedEvent = eventService.update(eventFormDto);

        //then
        Mockito.verify(confirmationService, Mockito.times(1)).save(any(Confirmation.class));
        Assertions.assertThat(updatedEvent).isNotNull();

        try (MockedStatic<ZonedDateTime> mockedLocalDateTime = Mockito.mockStatic(ZonedDateTime.class)) {
            mockedLocalDateTime.when(ZonedDateTime::now).thenReturn(mockedNow);
            Assertions.assertThat(updatedEvent.eventAttendanceIsClosed()).isFalse();
        }
    }

    @Test
    void onUpdateEventAttendance_AttendanceIsCreated2() {
        //given
        var eventFormDto = EventFormDto.builder().id(1).groupId(1).activateAttendanceList(true)
            .startDate(start).endDate(end).build();
        var existingEvent = new Event().setId(1).setGroupId(Group.GARAJONAY).setActiveAttendanceList(false);

        //when
        when(eventRepository.findById(anyInt())).thenReturn(Optional.of(existingEvent));
        when(scoutService.findAllByLoggedScouterGroupId()).thenReturn(List.of(new Scout().setId(1)));
        ZonedDateTime mockedNow = ZonedDateTime.parse("2025-03-13T12:00:00Z");
        mockSave();
        Event updatedEvent = eventService.update(eventFormDto);

        //then
        Mockito.verify(confirmationService, Mockito.times(1)).save(any(Confirmation.class));
        Assertions.assertThat(updatedEvent).isNotNull();

        try (MockedStatic<ZonedDateTime> mockedLocalDateTime = Mockito.mockStatic(ZonedDateTime.class)) {
            mockedLocalDateTime.when(ZonedDateTime::now).thenReturn(mockedNow);
            Assertions.assertThat(updatedEvent.eventAttendanceIsClosed()).isTrue();
        }
    }

    @Test
    void onUpdateEventAttendance_AttendanceDateCloseSet() {
        //given
        var eventFormDto = EventFormDto.builder().id(1).groupId(1).activateAttendanceList(true)
            .startDate(start).endDate(end).closeDateTime(ZonedDateTime.parse("2025-03-08T11:00:00Z")).build();
        var existingEvent = new Event().setId(1).setGroupId(Group.GARAJONAY)
            .setStartDate(start).setEndDate(end)
            .setActiveAttendanceList(true);
        ZonedDateTime mockedNow = ZonedDateTime.parse("2025-03-12T11:00:00Z");

        //before
        try (MockedStatic<ZonedDateTime> mockedLocalDateTime = Mockito.mockStatic(ZonedDateTime.class)) {
            mockedLocalDateTime.when(ZonedDateTime::now).thenReturn(mockedNow);
            Assertions.assertThat(existingEvent.eventAttendanceIsClosed()).isFalse();
        }

        //when
        when(eventRepository.findById(anyInt())).thenReturn(Optional.of(existingEvent));
        mockSave();
        Event updatedEvent = eventService.update(eventFormDto);

        //then
        Assertions.assertThat(updatedEvent).isNotNull();

        try (MockedStatic<ZonedDateTime> mockedLocalDateTime = Mockito.mockStatic(ZonedDateTime.class)) {
            mockedLocalDateTime.when(ZonedDateTime::now).thenReturn(mockedNow);
            Assertions.assertThat(updatedEvent.eventAttendanceIsClosed()).isTrue();
        }
    }

    @Test
    void onUpdateEventAttendance_AttendanceIsDeactivated() {
        //given
        var eventFormDto = EventFormDto.builder().id(1).groupId(1).activateAttendanceList(false)
            .startDate(start).endDate(end).build();
        var existingEvent = new Event().setId(1).setGroupId(Group.GARAJONAY).setActiveAttendanceList(true);

        //when
        when(eventRepository.findById(anyInt())).thenReturn(Optional.of(existingEvent));
        ZonedDateTime mockedNow = ZonedDateTime.parse("3999-03-13T12:00:00Z");
        mockSave();
        Event updatedEvent = eventService.update(eventFormDto);

        //then
        Mockito.verify(confirmationService, Mockito.times(1)).deleteAllByEventId(1);
        Assertions.assertThat(updatedEvent).isNotNull();

        try (MockedStatic<ZonedDateTime> mockedLocalDateTime = Mockito.mockStatic(ZonedDateTime.class)) {
            mockedLocalDateTime.when(ZonedDateTime::now).thenReturn(mockedNow);
            Assertions.assertThat(updatedEvent.eventAttendanceIsClosed()).isFalse();
        }
    }

    void mockSave() {
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private EventFormDto buildEventFormDto() {
        return new EventFormDto(
            1,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            false,
            false,
            false,
            null
        );
    }
}