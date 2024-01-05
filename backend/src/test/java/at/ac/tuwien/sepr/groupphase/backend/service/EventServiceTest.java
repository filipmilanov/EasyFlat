package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SharedFlatMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class EventServiceTest {
    @Autowired
    private EventsService eventsService;

    @MockBean
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SharedFlatMapper sharedFlatMapper;
    private ApplicationUser applicationUser;

    @Autowired
    private TestDataGenerator testDataGenerator;


    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();

        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);
    }

    @Test
    void givenValidEventDtoWhenCreateThenReturnCreatedEventDto() throws ValidationException {
        // given
        SharedFlat sharedFlat = new SharedFlat().setId(1L);


        EventDto eventDto = EventDtoBuilder.builder()
            .id(1L)
            .title("Test Title")
            .description("Test Description")
            .date(LocalDate.now().plusDays(1))
            .startTime(LocalTime.now())
            .endTime(LocalTime.now().plusMinutes(10))
            .sharedFlat(sharedFlatMapper.entityToWgDetailDto(sharedFlat))
            .build();


        // when
        EventDto result = eventsService.create(eventDto);

        //then
        assertAll(
            () -> assertThat(result.title()).isEqualTo(eventDto.title()),
            () -> assertThat(result.description()).isEqualTo(eventDto.description()),
            () -> assertThat(result.date()).isEqualTo(eventDto.date()),
            () -> assertThat(result.sharedFlat().getId()).isEqualTo(eventDto.sharedFlat().getId())
        );


    }

    @Test
    void givenUpdatedEventDtoWhenUpdateThenReturnUpdatedEventDto() throws AuthorizationException, ValidationException {

        //given
        EventDto updatedEventDto = EventDtoBuilder.builder()
            .id(1L)
            .title("Updated Title")
            .description("Updated Description")
            .date(LocalDate.now().plusDays(2))
            .startTime(LocalTime.now())
            .endTime(LocalTime.now().plusMinutes(10))
            .sharedFlat(sharedFlatMapper.entityToWgDetailDto(new SharedFlat().setId(1L)))
            .build();

        //when
        EventDto result = eventsService.update(updatedEventDto);

        //then
        assertAll(
            () -> assertThat(result.title()).isEqualTo(updatedEventDto.title()),
            () -> assertThat(result.description()).isEqualTo(updatedEventDto.description()),
            () -> assertThat(result.date()).isEqualTo(updatedEventDto.date()),
            () -> assertThat(result.sharedFlat().getId()).isEqualTo(updatedEventDto.sharedFlat().getId())
        );
    }

    @Test
    void givenInvalidEventDtoWithEmptyTitleWhenCreateThenThrowValidationException() {

        EventDto invalidEventDto = EventDtoBuilder.builder()
            .title("")
            .description("")
            .date(LocalDate.now().plusDays(2))
            .startTime(LocalTime.now())
            .endTime(LocalTime.now().plusMinutes(10))
            .sharedFlat(sharedFlatMapper.entityToWgDetailDto(new SharedFlat().setId(1L)))
            .build();


        assertThrows(ValidationException.class, () -> eventsService.create(invalidEventDto));
    }

    @Test
    void givenNonExistingEventIdWhenUpdateThenThrowEntityNotFoundException() {
        EventDto nonExistingEventDto = EventDtoBuilder.builder()
            .id(100L) // Assume ID 100 doesn't exist
            .title("Updated Title")
            .description("Updated Description")
            .date(LocalDate.now().plusDays(2))
            .startTime(LocalTime.now())
            .endTime(LocalTime.now().plusMinutes(10))
            .sharedFlat(sharedFlatMapper.entityToWgDetailDto(new SharedFlat().setId(1L)))
            .build();

        assertThrows(EntityNotFoundException.class, () -> eventsService.update(nonExistingEventDto));
    }

    @Test
    void givenUnauthorizedUserWhenUpdateThenThrowAuthorizationException() {
        applicationUser = userRepository.findById(2L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);

        EventDto eventDto = EventDtoBuilder.builder()
            .id(1L)
            .title("Updated Title")
            .description("Updated Description")
            .date(LocalDate.now().plusDays(2))
            .startTime(LocalTime.now())
            .endTime(LocalTime.now().plusMinutes(10))
            .sharedFlat(sharedFlatMapper.entityToWgDetailDto(new SharedFlat().setId(1L)))
            .build();

        assertThrows(AuthorizationException.class, () -> eventsService.update(eventDto));
    }

    @Test
    void givenValidEventIdWhenDeleteThenReturnDeletedEventDto() throws AuthorizationException {
        // given
        Long eventIdToDelete = 1L;

        // when
        EventDto result = eventsService.delete(eventIdToDelete);

        // then
        assertAll(
            () -> assertThat(result.title()).isNotNull(),
            () -> assertThat(result.description()).isNotNull(),
            () -> assertThat(result.date()).isNotNull(),
            () -> assertThat(result.sharedFlat().getId()).isNotNull()
        );
    }

    @Test
    void givenNonExistingEventIdWhenDeleteThenThrowEntityNotFoundException() {
        Long nonExistingEventId = 100L;

        assertThrows(EntityNotFoundException.class, () -> eventsService.delete(nonExistingEventId));
    }

    @Test
    void givenUnauthorizedUserWhenDeleteThenThrowAuthorizationException() {

        applicationUser = userRepository.findById(2L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);

        Long eventIdToDelete = 1L;

        assertThrows(AuthorizationException.class, () -> eventsService.delete(eventIdToDelete));
    }

    @Test
    void givenValidUserWhenFindAllThenReturnListOfEventDtos() {
        // given
        List<EventDto> result = eventsService.findAll();

        // then
        assertThat(result).isNotNull().isNotEmpty();
    }

    @Test
    void givenValidEventIdWhenGetEventWithIdThenReturnEventDto() throws AuthorizationException {
        // given
        Long eventIdToRetrieve = 1L;

        // when
        EventDto result = eventsService.getEventWithId(eventIdToRetrieve);

        // then
        assertAll(
            () -> assertThat(result.title()).isNotNull(),
            () -> assertThat(result.description()).isNotNull(),
            () -> assertThat(result.date()).isNotNull(),
            () -> assertThat(result.sharedFlat().getId()).isNotNull()
        );
    }

    @Test
    void givenNonExistingEventIdWhenGetEventWithIdThenThrowEntityNotFoundException() {

        Long nonExistingEventId = 100L;

        assertThrows(EntityNotFoundException.class, () -> eventsService.getEventWithId(nonExistingEventId));
    }

    @Test
    void givenInvalidEventDtoWithEndTimeBeforeStartTimeWhenCreateThenThrowValidationException() {

        EventDto invalidEventDto = EventDtoBuilder.builder()
            .title("Title")
            .startTime(LocalTime.of(12, 0))
            .endTime(LocalTime.of(11, 0))
            .date(LocalDate.now().plusDays(1))
            .sharedFlat(sharedFlatMapper.entityToWgDetailDto(new SharedFlat().setId(1L)))
            .build();

        assertThrows(ValidationException.class, () -> eventsService.create(invalidEventDto));
    }

    @Test
    @DisplayName("Positive test for exporting a valid event")
    void givenValidEventIdExportShouldSucceed() throws AuthorizationException {

       String exported = this.eventsService.exportEvent(1L);

       assertNotNull(exported);
    }

    @Test
    @DisplayName("Negative test for exporting a non-existing event")
    void givenInvalidEventIdExportShouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> this.eventsService.exportEvent(1000L));
    }
}
