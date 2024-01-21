package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.ChoreDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.CleanDatabase;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.SharedFlatDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChoreRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PreferenceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("unitTest")
public class PreferenceServiceTest {

    @Autowired
    private SharedFlatDataGenerator sharedFlatDataGenerator;

    @Autowired
    private ChoreDataGenerator choreDataGenerator;

    @Autowired
    private PreferenceService preferenceService;

    @MockBean
    private AuthService authService;

    @Autowired
    private PreferenceRepository preferenceRepository;

    @Autowired
    private ChoreRepository choreRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CleanDatabase cleanDatabase;

    private ApplicationUser testUser = new ApplicationUser(1L, "FirstName", "LastName", "user@email.com", "password", Boolean.FALSE, null);

    private PreferenceDto preferenceDto;

    private ChoreDto first;

    private ChoreDto second;

    private ChoreDto third;

    private ChoreDto fourth;
    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        cleanDatabase.truncateAllTablesAndRestartIds();
        sharedFlatDataGenerator.generateSharedFlats();
        choreDataGenerator.generateChores();
        testUser.setPoints(0);
        testUser.setSharedFlat(new SharedFlat().setId(1L));
        userRepository.save(testUser);
        when(authService.getUserFromToken()).thenReturn(testUser);

        first = new ChoreDto(
            null,
            "First",
            "",
            LocalDate.of(2022, 8, 18),
            5,
            null
        );
        second = new ChoreDto(
            null,
            "Second",
            "Description for Chore 2",
            LocalDate.of(2022, 8, 18),
            5,
            null
        );
        third = new ChoreDto(
            null,
            "Third",
            "",
            LocalDate.of(2022, 8, 18),
            5,
            null
        );
        fourth = new ChoreDto(
            null,
            "Fourth",
            "",
            LocalDate.of(2022, 8, 18),
            5,
            null
        );

        preferenceDto = new PreferenceDto(
            null,
            first,
            second,
            third,
            fourth
        );
    }

    @Test
    void updateWithValidPreferenceDtoShouldSucceed()  {
    }

    @Test
    void getLastPreferenceShouldSucceed() {

    }
}
