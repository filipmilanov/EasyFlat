package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SharedFlatMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class EventEndpointTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private TestDataGenerator testDataGenerator;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private AuthService authService;

    private final String BASE_URI = "/api/v1/events";
    private ApplicationUser applicationUser;
    @Autowired
    private SharedFlatMapper sharedFlatMapper;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();

        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);
    }

    @Test
    public void givenEventWhenCreateThenEventIsCreated() throws Exception {
        // given
        EventDto eventDto = new EventDto(null, "Test Event", "Description", LocalDate.now().plusDays(1), null,null);


        // when
        MvcResult mvcResult = this.mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto))
                .header("Authorization", "Bearer " + jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        EventDto createdEvent = objectMapper.readValue(response.getContentAsString(), EventDto.class);

        assertAll(
            () -> assertNotNull(createdEvent.id()),
            () -> assertEquals(eventDto.title(), createdEvent.title()),
            () -> assertEquals(eventDto.description(), createdEvent.description()),
            () -> assertEquals(eventDto.date(), createdEvent.date())
        );
    }

    @Test
    public void givenInvalidEventWhenCreateThenValidationException() throws Exception {
        // given
        EventDto invalidEventDto = new EventDto(null, "", "", LocalDate.now(), null,null);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidEventDto))
                .header("Authorization", "Bearer " + jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());


    }

    @Test
    public void givenExistingEventIdWhenGetEventWithIdThenReturnEvent() throws Exception {
        // given
        Long eventId = 1L;
        SharedFlat sharedFlat = new SharedFlat().setId(1L);
        EventDto eventDto = new EventDto(eventId, "House Meeting", "Discussing important matters regarding the shared living space.", LocalDate.now().plusDays(7), sharedFlatMapper.entityToWgDetailDto(sharedFlat),null);


        // when
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI + "/{id}", eventId)
                .header("Authorization", "Bearer " + jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        EventDto retrievedEvent = objectMapper.readValue(response.getContentAsString(), EventDto.class);

        assertAll(
            () -> assertNotNull(retrievedEvent.id()),
            () -> assertEquals(eventDto.title(), retrievedEvent.title()),
            () -> assertEquals(eventDto.description(), retrievedEvent.description()),
            () -> assertEquals(eventDto.date(), retrievedEvent.date()),
            () -> assertEquals(eventDto.sharedFlat().getName(), retrievedEvent.sharedFlat().getName())
        );
    }

    @Test
    public void givenValidEventIdWhenGetEventWithIdThenReturnEvent() throws Exception {
        // given
        Long validEventId = 1L;

        // when
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI + "/{id}", validEventId)
                .header("Authorization", "Bearer " + jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        EventDto retrievedEvent = objectMapper.readValue(response.getContentAsString(), EventDto.class);

        assertAll(
            () -> assertNotNull(retrievedEvent.id()),
            () -> assertEquals(validEventId, retrievedEvent.id())
        );
    }

    @Test
    public void givenEventWithInvalidDateWhenCreateThenValidationException() throws Exception {
        // given
        EventDto invalidDateEvent = new EventDto(null, "Invalid Date Event", "Description", LocalDate.now().minusDays(22), null,null);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDateEvent))
                .header("Authorization", "Bearer " + jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

}
