package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest implements TestData {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Positive test for registering a valid user")
    public void registerValidUserAndCheckIfSuccessfullyRegistered() throws ValidationException, ConflictException {
        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setFirstName("John");
        userDetailDto.setLastName("Doe");
        userDetailDto.setEmail("john.doe@example.com");
        userDetailDto.setPassword("password");

        String authToken = userService.register(userDetailDto);

        ApplicationUser registeredUser = userRepository.findUserByEmail("john.doe@example.com");
        assertNotNull(registeredUser);
        assertEquals("John", registeredUser.getFirstName());
        assertEquals("Doe", registeredUser.getLastName());
        assertEquals("john.doe@example.com", registeredUser.getEmail());
        assertEquals(passwordEncoder.matches("password",registeredUser.getPassword()),true );
        assertNotNull(authToken);
    }

    @Test
    @DisplayName("Negative test for registering a user with an existing email")
    public void registerUserWithExistingEmailShouldThrowException() throws ValidationException, ConflictException {
        UserDetailDto existingUser = new UserDetailDto();
        existingUser.setFirstName("Alice");
        existingUser.setLastName("Smith");
        existingUser.setEmail("alice@example.com");
        existingUser.setPassword("password");
        userService.register(existingUser);

        UserDetailDto newUser = new UserDetailDto();
        newUser.setFirstName("Bob");
        newUser.setLastName("Johnson");
        newUser.setEmail("alice@example.com");
        newUser.setPassword("password");

        assertThrows(Exception.class, () -> userService.register(newUser));
    }

    @Test
    @DisplayName("Positive test for updating an existing user with valid data")
    public void updateExistingUserWithValidData() throws ValidationException, ConflictException {
        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setFirstName("John");
        userDetailDto.setLastName("Doe");
        userDetailDto.setEmail("john@example.com");
        userDetailDto.setPassword("password");
        userService.register(userDetailDto);

        userDetailDto.setFirstName("UpdatedFirstName");
        userDetailDto.setLastName("UpdatedLastName");
        userDetailDto.setPassword("newpassword123");
        userService.update(userDetailDto);

        ApplicationUser fetchedUser = userRepository.findUserByEmail("john@example.com");
        assertNotNull(fetchedUser);
        assertEquals("UpdatedFirstName", fetchedUser.getFirstName());
        assertEquals("UpdatedLastName", fetchedUser.getLastName());
        assertEquals(passwordEncoder.matches("newpassword123",fetchedUser.getPassword()),true );
    }

    @Test
    @DisplayName("Positive test for deleting an existing user")
    @Disabled
    public void deleteExistingUserAndEnsureDeletion() throws ValidationException, ConflictException {
        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setFirstName("ToDelete");
        userDetailDto.setLastName("User");
        userDetailDto.setEmail("todelete@example.com");
        userDetailDto.setPassword("password");
        userService.register(userDetailDto);

        ApplicationUser user = userRepository.findUserByEmail(userDetailDto.getEmail());

        userService.delete(user.getId());


        ApplicationUser deletedUserFromDB = userRepository.findUserByEmail("todelete@example.com");
        assertNull(deletedUserFromDB, "Deleted user should not be found");
    }

}
