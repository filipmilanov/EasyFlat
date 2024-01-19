package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ChoreMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChoreRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SharedFlatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.ChoreServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.interfaces.ChoreValidator;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.CleanDatabase;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@SpringBootTest
@ActiveProfiles("unitTest")
public class ChoreServiceTest {
    @MockBean
    private ChoreValidator choreValidator;

    @Autowired
    private ChoreMapper choreMapper;

    @MockBean
    private AuthService authService;

    @Autowired
    private ChoreServiceImpl choreService;

    private ApplicationUser testUser = new ApplicationUser(1L, "FirstName", "LastName", "user@email.com", "password", Boolean.FALSE, null);


    private SharedFlat sharedFlat;

    @Autowired
    private CleanDatabase cleanDatabase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChoreRepository choreRepository;

    @Autowired
    private SharedFlatRepository sharedFlatRepository;

    private ChoreDto validChoreDto;
    private ChoreDto invalidChoreDto;

    private Faker faker = new Faker(new Random(24012024));

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        cleanDatabase.truncateAllTablesAndRestartIds();
        sharedFlat = sharedFlatRepository.save(new SharedFlat());
        testUser.setPoints(0);
        testUser.setSharedFlat(sharedFlat);
        userRepository.save(testUser);
        when(authService.getUserFromToken()).thenReturn(testUser);
        validChoreDto = new ChoreDto(
            null,
            "Chore 1",
            "Description for Chore 1",
            LocalDate.of(2022, 8, 18),
            5,
            null
        );
        invalidChoreDto = new ChoreDto(
            2L,
            "Chore 2",
            "Description for Chore 2",
            LocalDate.of(2022,8,18),
            -5,
            null
        );
    }

    @Test
    public void createValidChoreShouldSucceed() throws ValidationException, ConflictException {
        doNothing().when(choreValidator).validateForCreate(
            eq(validChoreDto)
        );

        ChoreDto result = choreService.createChore(validChoreDto);

        assertAll(
            () -> assertNotNull(result),
            () -> assertNotNull(result.id()),
            () -> assertEquals(validChoreDto.name(), result.name()),
            () -> assertEquals(validChoreDto.description(), result.description()),
            () -> assertEquals(validChoreDto.endDate(), result.endDate()),
            () -> assertEquals(validChoreDto.points(), result.points()),
            () -> assertNull(result.user())
        );
    }

    @Test
    public void getChoresShouldSucceed() throws AuthenticationException {
        Chore chore1 = new Chore();
        chore1.setName("Chore 1");
        chore1.setDescription("Description");
        chore1.setPoints(5);
        chore1.setEndDate(LocalDate.of(2022,8,18));
        chore1.setUser(null);
        chore1.setSharedFlat(sharedFlat);

        choreRepository.save(chore1);

        List<Chore> chores = choreService.getChores(new ChoreSearchDto(null, null));

        assertAll(
            () -> assertNotNull(chores),
            () -> assertEquals(1, chores.size()),
            () -> assertEquals(chore1, chores.get(0))
        );
    }

    @Test
    public void assignChoreShouldSucceed() {
        Chore chore1 = new Chore();
        chore1.setName("Chore 1");
        chore1.setDescription("Description");
        chore1.setPoints(5);
        chore1.setEndDate(LocalDate.of(2022,8,18));
        chore1.setUser(null);
        chore1.setSharedFlat(sharedFlat);

        choreRepository.save(chore1);

        Chore chore2 = new Chore();
        chore2.setName("Chore 2");
        chore2.setDescription("Description2");
        chore2.setPoints(5);
        chore2.setEndDate(LocalDate.of(2022,8,18));
        chore2.setUser(null);
        chore2.setSharedFlat(sharedFlat);

        choreRepository.save(chore2);

        Preference pref1 = new Preference();
        pref1.setFirst(chore1);
        testUser.setPreference(pref1);
        userRepository.save(testUser);

        ApplicationUser testUser2 = new ApplicationUser(2L, "FirstName2", "LastName2", "user2@email.com", "password", Boolean.FALSE, sharedFlat);

        Preference pref2 = new Preference();
        pref1.setFirst(chore2);
        testUser2.setPreference(pref2);
        userRepository.save(testUser2);



    }

}
