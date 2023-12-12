package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Profile({"generateData", "test"})
@Component("ApplicationUserDataGenerator")
@DependsOn({"CleanDatabase", "SharedFlatDataGenerator"})
public class ApplicationUserDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ENTITIES_TO_GENERATE = 5;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ApplicationUserDataGenerator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void generateApplicationUsers() {
        LOGGER.debug("generating {} User Entities", NUMBER_OF_ENTITIES_TO_GENERATE);
        for (int i = 0; i < NUMBER_OF_ENTITIES_TO_GENERATE; i++) {
            ApplicationUser user = new ApplicationUser();
            user.setFirstName("First" + (i + 1));
            user.setLastName("Last" + (i + 1));
            user.setEmail("user" + (i + 1) + "@email.at");
            user.setPassword(passwordEncoder.encode("12341234"));
            user.setAdmin(false);

            SharedFlat sharedFlat = new SharedFlat();
            sharedFlat.setId((long) (i + 1));

            user.setSharedFlat(sharedFlat);

            LOGGER.debug("saving user: {}", user);
            userRepository.save(user);
        }
    }
}
