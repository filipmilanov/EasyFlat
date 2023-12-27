package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SharedFlatMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class EventServiceTest {
    @Autowired
    private EventsService eventsService;

    @Autowired
    private SharedFlatMapper sharedFlatMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SharedFlatService sharedFlatService;

    private ApplicationUser applicationUser;

    @Autowired
    private TestDataGenerator testDataGenerator;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        sharedFlat.setPassword("$2a$10$eCcWPTLzy8RukpbQPporF.79pNeBHgn.0YwT9u96eBrxnaySHRuNu");

        EventDto eventDto = EventDtoBuilder.builder()
            .id(1L)
            .title("Test Title")
            .description("Test Description")
            .date(LocalDate.now().plusDays(1))
            .sharedFlat(sharedFlatMapper.entityToWgDetailDto(sharedFlat))
            .build();


        // when
        EventDto result = eventsService.create(eventDto);


        assertAll(
            () -> assertThat(result.title()).isEqualTo(eventDto.title()),
            () -> assertThat(result.description()).isEqualTo(eventDto.description()),
            () -> assertThat(result.date()).isEqualTo(eventDto.date()),
            () -> assertThat(result.sharedFlat().getId()).isEqualTo(eventDto.sharedFlat().getId())
        );


    }


}
