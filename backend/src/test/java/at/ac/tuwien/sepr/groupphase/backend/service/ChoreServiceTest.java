package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.ChoreDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.CleanDatabase;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.SharedFlatDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ChoreMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChoreRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PreferenceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SharedFlatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.ChoreServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.interfaces.ChoreValidator;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("unitTest")
public class ChoreServiceTest {

    @Autowired
    private SharedFlatDataGenerator sharedFlatDataGenerator;
    @Autowired
    private ChoreDataGenerator choreDataGenerator;
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
    private PreferenceRepository preferenceRepository;

    @Autowired
    private ChoreRepository choreRepository;

    private ChoreDto validChoreDto;
    private ChoreDto invalidChoreDto;

    private Faker faker = new Faker(new Random(24012024));

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        cleanDatabase.truncateAllTablesAndRestartIds();
        sharedFlatDataGenerator.generateSharedFlats();
        choreDataGenerator.generateChores();
        testUser.setPoints(0);
        testUser.setSharedFlat(new SharedFlat().setId(1L));
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
            LocalDate.of(2022, 8, 18),
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
        List<Chore> chores = choreService.getChores(new ChoreSearchDto(null, null));

        assertAll(
            () -> assertNotNull(chores),
            () -> assertEquals(5, chores.size())
        );
    }

    @Test
    public void assignChoreShouldSucceed() throws AuthenticationException {
        //Data
        Preference pref1 = new Preference();
        pref1.setFirst(new Chore().setId(1L));
        pref1.setSecond(new Chore().setId(2L));
        pref1.setThird(new Chore().setId(3L));
        pref1.setUserId(testUser);
        preferenceRepository.save(pref1);

        testUser.setPreference(pref1);
        userRepository.save(testUser);

        ApplicationUser testUser2 = new ApplicationUser(2L, "FirstName2", "LastName2", "user2@email.com", "password", Boolean.FALSE, testUser.getSharedFlat());
        userRepository.save(testUser2);

        Preference pref2 = new Preference();
        pref2.setFirst(new Chore().setId(4L));
        pref2.setSecond(new Chore().setId(5L));
        pref2.setUserId(testUser2);
        preferenceRepository.save(pref2);

        testUser2.setPreference(pref2);
        testUser2.setPoints(0);
        userRepository.save(testUser2);
        //Test
        choreService.assignChores();

        List<Chore> choresAfterAssign = choreRepository.findAllBySharedFlatIdWhereUserIsNull(testUser.getSharedFlat().getId());
        List<Chore> choresTestUser = choreRepository.findAllByUser(testUser);
        List<Chore> choresTestUser2 = choreRepository.findAllByUser(testUser2);
        //Results
        assertAll(
            //checks if all the chores are assigned
            () -> assertEquals(0, choresAfterAssign.size()),
            //checks that the first person has 2 out of 5 chores
            () -> assertEquals(2, choresTestUser.size()),
            //checks that the last person has 3 out of 5 chores
            () -> assertEquals(3, choresTestUser2.size()),
            () -> assertTrue(choresTestUser.contains(pref1.getFirst())),
            () -> assertTrue(choresTestUser.contains(pref1.getSecond())),
            () -> assertTrue(choresTestUser2.contains(pref1.getThird())),
            () -> assertTrue(choresTestUser2.contains(pref2.getSecond())),
            () -> assertTrue(choresTestUser2.contains(pref2.getFirst()))
        );
    }

    @Test
    public void assignChoreShouldFail() {
        //Data
        List<Chore> chores = choreRepository.findAllBySharedFlatId(testUser.getSharedFlat().getId());
        choreRepository.deleteAll(chores);
        //Result + Test
        assertThrows(NotFoundException.class, () -> choreService.assignChores());
    }

    @Test
    public void secondAssignChoreShouldSucceed() throws AuthenticationException {
        //Data
        Preference pref1 = new Preference();
        pref1.setFirst(new Chore().setId(1L));
        pref1.setSecond(new Chore().setId(2L));
        pref1.setThird(new Chore().setId(3L));
        pref1.setUserId(testUser);
        preferenceRepository.save(pref1);

        testUser.setPreference(pref1);
        userRepository.save(testUser);

        ApplicationUser testUser2 = new ApplicationUser(2L, "FirstName2", "LastName2", "user2@email.com", "password", Boolean.FALSE, testUser.getSharedFlat());
        userRepository.save(testUser2);

        Preference pref2 = new Preference();
        pref2.setFirst(new Chore().setId(4L));
        pref2.setSecond(new Chore().setId(5L));
        pref2.setUserId(testUser2);
        preferenceRepository.save(pref2);

        testUser2.setPreference(pref2);
        testUser2.setPoints(0);
        userRepository.save(testUser2);

        choreService.assignChores();

        Chore newChore = new Chore();
        newChore.setName("New Chore");
        newChore.setSharedFlat(testUser.getSharedFlat());
        choreRepository.save(newChore);
        //Test
        choreService.assignChores();

        List<Chore> choresAfterAssign = choreRepository.findAllBySharedFlatIdWhereUserIsNull(testUser.getSharedFlat().getId());
        List<Chore> choresTestUser = choreRepository.findAllByUser(testUser);
        List<Chore> choresTestUser2 = choreRepository.findAllByUser(testUser2);

        //Results
        assertAll(
            //checks if all the chores are assigned
            () -> assertEquals(0, choresAfterAssign.size()),
            // the first person has 2 out of 5 chores
            // now he has one more, so the assigned chores are equal
            () -> assertEquals(3, choresTestUser.size()),
            //checks that the last person has 3 out of 5 chores
            () -> assertEquals(3, choresTestUser2.size()),
            () -> assertTrue(choresTestUser.contains(pref1.getFirst())),
            () -> assertTrue(choresTestUser.contains(pref1.getSecond())),
            () -> assertTrue(choresTestUser2.contains(pref1.getThird())),
            () -> assertTrue(choresTestUser2.contains(pref2.getSecond())),
            () -> assertTrue(choresTestUser2.contains(pref2.getFirst())),
            //ChoreDataGenerator generates 25 chores, the new one is with id = 26
            () -> assertTrue(choresTestUser.contains(new Chore().setId(26L)))
        );
    }

    @Test
    public void secondAssignChoreShouldFail() throws AuthenticationException {
        //Data
        Preference pref1 = new Preference();
        pref1.setFirst(new Chore().setId(1L));
        pref1.setSecond(new Chore().setId(2L));
        pref1.setThird(new Chore().setId(3L));
        pref1.setUserId(testUser);
        preferenceRepository.save(pref1);

        testUser.setPreference(pref1);
        userRepository.save(testUser);

        ApplicationUser testUser2 = new ApplicationUser(2L, "FirstName2", "LastName2", "user2@email.com", "password", Boolean.FALSE, testUser.getSharedFlat());
        userRepository.save(testUser2);

        Preference pref2 = new Preference();
        pref2.setFirst(new Chore().setId(4L));
        pref2.setSecond(new Chore().setId(5L));
        pref2.setUserId(testUser2);
        preferenceRepository.save(pref2);

        testUser2.setPreference(pref2);
        testUser2.setPoints(0);
        userRepository.save(testUser2);

        choreService.assignChores();
        //Result + Test
        assertThrows(NotFoundException.class, () -> choreService.assignChores());
    }

    @Test
    public void assignChoreWithMoreUsersAndPointsShouldSucceed() throws AuthenticationException {
        //Data
        testUser.setPoints(10);
        testUser.setSharedFlat(new SharedFlat().setId(1L));
        userRepository.save(testUser);
        ApplicationUser testUser1 = new ApplicationUser(2L, "FirstName1", "LastName1", "user1@email.com", "password", Boolean.FALSE, new SharedFlat().setId(1L));
        testUser1.setPoints(9);
        userRepository.save(testUser1);
        ApplicationUser testUser2 = new ApplicationUser(3L, "FirstName2", "LastName2", "user2@email.com", "password", Boolean.FALSE, new SharedFlat().setId(1L));
        testUser2.setPoints(8);
        userRepository.save(testUser2);
        ApplicationUser testUser3 = new ApplicationUser(4L, "FirstName3", "LastName3", "user3@email.com", "password", Boolean.FALSE, new SharedFlat().setId(1L));
        testUser3.setPoints(7);
        userRepository.save(testUser3);
        ApplicationUser testUser4 = new ApplicationUser(5L, "FirstName4", "LastName4", "user4@email.com", "password", Boolean.FALSE, new SharedFlat().setId(1L));
        testUser4.setPoints(6);
        userRepository.save(testUser4);
        ApplicationUser testUser5 = new ApplicationUser(6L, "FirstName5", "LastName5", "user5@email.com", "password", Boolean.FALSE, new SharedFlat().setId(1L));
        testUser5.setPoints(5);
        userRepository.save(testUser5);

        //Test
        choreService.assignChores();

        //Results
        assertAll(
            () -> assertNotNull(choreRepository.findAllByUser(testUser1)),
            () -> assertNotNull(choreRepository.findAllByUser(testUser2)),
            () -> assertNotNull(choreRepository.findAllByUser(testUser3)),
            () -> assertNotNull(choreRepository.findAllByUser(testUser4)),
            () -> assertNotNull(choreRepository.findAllByUser(testUser5))
        );

    }

    @Test
    public void secondAssignChoreWithMoreUsersAndPointsShouldSucceed() throws AuthenticationException {
        //Data
        testUser.setPoints(10);
        testUser.setSharedFlat(new SharedFlat().setId(1L));
        userRepository.save(testUser);
        ApplicationUser testUser1 = new ApplicationUser(2L, "FirstName1", "LastName1", "user1@email.com", "password", Boolean.FALSE, new SharedFlat().setId(1L));
        testUser1.setPoints(9);
        userRepository.save(testUser1);
        ApplicationUser testUser2 = new ApplicationUser(3L, "FirstName2", "LastName2", "user2@email.com", "password", Boolean.FALSE, new SharedFlat().setId(1L));
        testUser2.setPoints(8);
        userRepository.save(testUser2);
        ApplicationUser testUser3 = new ApplicationUser(4L, "FirstName3", "LastName3", "user3@email.com", "password", Boolean.FALSE, new SharedFlat().setId(1L));
        testUser3.setPoints(7);
        userRepository.save(testUser3);
        ApplicationUser testUser4 = new ApplicationUser(5L, "FirstName4", "LastName4", "user4@email.com", "password", Boolean.FALSE, new SharedFlat().setId(1L));
        testUser4.setPoints(6);
        userRepository.save(testUser4);
        ApplicationUser testUser5 = new ApplicationUser(6L, "FirstName5", "LastName5", "user5@email.com", "password", Boolean.FALSE, new SharedFlat().setId(1L));
        testUser5.setPoints(5);
        userRepository.save(testUser5);

        choreService.assignChores();

        Chore newChore = new Chore();
        newChore.setName("New Chore");
        newChore.setSharedFlat(testUser.getSharedFlat());
        choreRepository.save(newChore);

        Chore newChore1 = new Chore();
        newChore1.setName("New Chore1");
        newChore1.setSharedFlat(testUser.getSharedFlat());
        choreRepository.save(newChore1);

        //Test
        choreService.assignChores();
        //Results
        assertAll(
            () -> assertNotNull(choreRepository.findAllByUser(testUser)),
            () -> assertNotNull(choreRepository.findAllByUser(testUser1)),
            () -> assertNotNull(choreRepository.findAllByUser(testUser2)),
            () -> assertNotNull(choreRepository.findAllByUser(testUser3)),
            () -> assertNotNull(choreRepository.findAllByUser(testUser4)),
            //this user is the last in Leaderboard, so he has one chore more
            () -> assertEquals(2,choreRepository.findAllByUser(testUser5).size())
        );

    }

    @Test
    public void secondAssignChoreWithMoreUsersPointsAndPreferenceShouldSucceed() throws AuthenticationException {
        //Data
        Preference pref = new Preference();
        pref.setFirst(new Chore().setId(1L));
        pref.setSecond(new Chore().setId(2L));
        pref.setThird(new Chore().setId(3L));
        pref.setFourth(new Chore().setId(4L));
        pref.setUserId(testUser);
        preferenceRepository.save(pref);
        testUser.setPreference(pref);
        testUser.setPoints(10);
        testUser.setSharedFlat(new SharedFlat().setId(1L));
        userRepository.save(testUser);


        ApplicationUser testUser1 = new ApplicationUser(2L, "FirstName1", "LastName1", "user1@email.com", "password", Boolean.FALSE, new SharedFlat().setId(1L));
        userRepository.save(testUser1);
        Preference pref1 = new Preference();
        pref1.setFirst(new Chore().setId(1L));
        pref1.setSecond(new Chore().setId(2L));
        pref1.setThird(new Chore().setId(3L));
        pref1.setFourth(new Chore().setId(4L));
        pref1.setUserId(testUser1);
        preferenceRepository.save(pref1);
        testUser1.setPreference(pref1);
        testUser1.setPoints(9);
        userRepository.save(testUser1);

        ApplicationUser testUser2 = new ApplicationUser(3L, "FirstName2", "LastName2", "user2@email.com", "password", Boolean.FALSE, new SharedFlat().setId(1L));
        userRepository.save(testUser2);
        Preference pref2 = new Preference();
        pref2.setFirst(new Chore().setId(1L));
        pref2.setSecond(new Chore().setId(2L));
        pref2.setThird(new Chore().setId(3L));
        pref2.setFourth(new Chore().setId(4L));
        pref2.setUserId(testUser2);
        preferenceRepository.save(pref2);
        testUser2.setPreference(pref2);
        testUser2.setPoints(8);
        userRepository.save(testUser2);

        ApplicationUser testUser3 = new ApplicationUser(4L, "FirstName3", "LastName3", "user3@email.com", "password", Boolean.FALSE, new SharedFlat().setId(1L));
        userRepository.save(testUser3);
        Preference pref3 = new Preference();
        pref3.setFirst(new Chore().setId(4L));
        pref3.setSecond(new Chore().setId(5L));
        pref3.setThird(new Chore().setId(3L));
        pref3.setFourth(new Chore().setId(2L));
        pref3.setUserId(testUser3);
        preferenceRepository.save(pref3);
        testUser2.setPreference(pref3);
        testUser3.setPoints(7);
        userRepository.save(testUser3);

        ApplicationUser testUser4 = new ApplicationUser(5L, "FirstName4", "LastName4", "user4@email.com", "password", Boolean.FALSE, new SharedFlat().setId(1L));
        userRepository.save(testUser4);
        Preference pref4 = new Preference();
        pref4.setFirst(new Chore().setId(5L));
        pref4.setSecond(new Chore().setId(4L));
        pref4.setThird(new Chore().setId(3L));
        pref4.setFourth(new Chore().setId(2L));
        pref4.setUserId(testUser4);
        preferenceRepository.save(pref4);
        testUser4.setPreference(pref4);
        testUser4.setPoints(6);
        userRepository.save(testUser4);

        ApplicationUser testUser5 = new ApplicationUser(6L, "FirstName5", "LastName5", "user5@email.com", "password", Boolean.FALSE, new SharedFlat().setId(1L));
        userRepository.save(testUser5);
        Preference pref5 = new Preference();
        pref5.setFirst(new Chore().setId(1L));
        pref5.setSecond(new Chore().setId(2L));
        pref5.setThird(new Chore().setId(3L));
        pref5.setFourth(new Chore().setId(4L));
        pref5.setUserId(testUser5);
        preferenceRepository.save(pref5);
        testUser2.setPreference(pref5);
        testUser5.setPoints(5);
        userRepository.save(testUser5);

        choreService.assignChores();

        Chore newChore = new Chore();
        newChore.setName("New Chore");
        newChore.setSharedFlat(testUser.getSharedFlat());
        choreRepository.save(newChore);

        Chore newChore1 = new Chore();
        newChore1.setName("New Chore1");
        newChore1.setSharedFlat(testUser.getSharedFlat());
        choreRepository.save(newChore1);

        //Test
        choreService.assignChores();
        //Results
        assertAll(
            () -> assertNotNull(choreRepository.findAllByUser(testUser)),
            () -> assertEquals(new Chore().setId(1L), choreRepository.findAllByUser(testUser1).get(0)),
            () -> assertEquals(new Chore().setId(2L), choreRepository.findAllByUser(testUser2).get(0)),
            () -> assertEquals(new Chore().setId(4L), choreRepository.findAllByUser(testUser3).get(0)),
            () -> assertEquals(new Chore().setId(5L), choreRepository.findAllByUser(testUser4).get(0)),
            () -> assertEquals(new Chore().setId(3L), choreRepository.findAllByUser(testUser5).get(0)),
            //this user is the last in Leaderboard, so he has one chore more
            () -> assertEquals(2,choreRepository.findAllByUser(testUser5).size())
        );

    }

}
