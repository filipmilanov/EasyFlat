package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
@ActiveProfiles("test")
public class EventServiceTest {
    @Autowired
    private EventsService eventsService;

    @Test
    @Disabled
    void givenValidEventDtoWhenCreateThenReturnCreatedEventDto() {
        // given
        EventDto eventDto = EventDtoBuilder.builder()
            .title("Test Title")
            .description("Test Description")
            .date(LocalDate.now().plusDays(1))
            .build();

        // when
        EventDto result = eventsService.create(eventDto);

        // then
        assertThat(result).isEqualTo(eventDto);

    }




}
