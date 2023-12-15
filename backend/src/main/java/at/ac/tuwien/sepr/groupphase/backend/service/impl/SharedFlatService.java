package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SharedFlatMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.DigitalStorageRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SharedFlatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.SharedFlatValidator;
import jakarta.transaction.Transactional;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.authenticator.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

@Service
public class SharedFlatService implements at.ac.tuwien.sepr.groupphase.backend.service.SharedFlatService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SharedFlatRepository sharedFlatRepository;
    private final PasswordEncoder passwordEncoder;
    private final SharedFlatMapper sharedFlatMapper;
    private final UserRepository userRepository;
    private final JwtTokenizer jwtTokenizer;
    private final Authorization authorization;
    private final DigitalStorageRepository digitalStorageRepository;
    private final ShoppingListRepository shoppingListRepository;

    private final SharedFlatValidator validator;

    @Autowired
    public SharedFlatService(SharedFlatRepository sharedFlatRepository,
                             PasswordEncoder passwordEncoder,
                             SharedFlatMapper sharedFlatMapper,
                             JwtTokenizer jwtTokenizer,
                             UserRepository userRepository,
                             Authorization authorization,
                             DigitalStorageRepository digitalStorageRepository,
                             ShoppingListRepository shoppingListRepository,
                             SharedFlatValidator validator) {
        this.sharedFlatRepository = sharedFlatRepository;
        this.passwordEncoder = passwordEncoder;
        this.sharedFlatMapper = sharedFlatMapper;
        this.jwtTokenizer = jwtTokenizer;
        this.userRepository = userRepository;
        this.authorization = authorization;
        this.digitalStorageRepository = digitalStorageRepository;
        this.shoppingListRepository = shoppingListRepository;
        this.validator = validator;
    }

    @Override
    public SharedFlat findById(Long id, String jwt) throws AuthenticationException {
        LOGGER.trace("findById({}, {})", id, jwt);

        Optional<SharedFlat> sharedFlatOptional = sharedFlatRepository.findById(id);
        SharedFlat sharedFlat = sharedFlatOptional.orElseThrow(() -> new NotFoundException("Shared flat not found"));

        authorization.authenticateUser(
            jwt,
            sharedFlat.getUsers().stream().map(ApplicationUser::getId).toList(),
            "User does not have access to this shared flat"
        );

        return sharedFlat;
    }

    public WgDetailDto create(SharedFlat sharedFlat, String authToken) throws ConflictException, ValidationException {
        LOGGER.trace("create({}, {})", sharedFlat, authToken);
        LOGGER.debug("Create a new shared flat");
        validator.validateForCreate(sharedFlat);
        SharedFlat existingSharedFlat = sharedFlatRepository.findFirstByName(sharedFlat.getName());
        if (existingSharedFlat != null) {
            throw new ConflictException("A flat with this name already exists.");
        }

        SharedFlat newSharedFlat = new SharedFlat();
        newSharedFlat.setName(sharedFlat.getName());
        newSharedFlat.setPassword(passwordEncoder.encode(sharedFlat.getPassword()));
        String userEmail = jwtTokenizer.getEmailFromToken(authToken);
        ApplicationUser user = userRepository.findUserByEmail(userEmail);
        sharedFlatRepository.save(newSharedFlat);
        user.setSharedFlat(newSharedFlat);
        user.setAdmin(true);
        userRepository.save(user);
        DigitalStorage digitalStorage = new DigitalStorage();
        digitalStorage.setTitle("Storage");
        digitalStorage.setSharedFlat(newSharedFlat);
        newSharedFlat.setDigitalStorage(digitalStorage);

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setName("Default");
        shoppingList.setSharedFlat(newSharedFlat);
        shoppingListRepository.save(shoppingList);

        digitalStorageRepository.save(digitalStorage);
        return sharedFlatMapper.entityToWgDetailDto(newSharedFlat);
    }

    @Override
    public WgDetailDto loginWg(SharedFlat wgDetailDto, String authToken) {
        LOGGER.trace("loginWg({}, {})", wgDetailDto, authToken);
        String name = wgDetailDto.getName();
        String rawPassword = wgDetailDto.getPassword();
        String userEmail = jwtTokenizer.getEmailFromToken(authToken);
        ApplicationUser user = userRepository.findUserByEmail(userEmail);

        SharedFlat existingSharedFlat = sharedFlatRepository.findFirstByName(name);


        if (existingSharedFlat != null) {
            boolean passwordMatches = passwordEncoder.matches(rawPassword, existingSharedFlat.getPassword());

            if (passwordMatches) {
                if (userEmail != null) {
                    user.setSharedFlat(existingSharedFlat);
                    user.setAdmin(false);
                    userRepository.save(user);
                }
                return sharedFlatMapper.entityToWgDetailDto(existingSharedFlat);
            } else {
                throw new IllegalStateException("Invalid credentials. Could not log in.");
            }
        } else {
            throw new IllegalStateException("Invalid credentials. Could not log in.");
        }
    }

    @Override
    @Transactional
    public WgDetailDto delete(String email) {
        LOGGER.trace("delete({})", email);
        ApplicationUser user = userRepository.findUserByEmail(email);
        if (!user.getAdmin()) {
            throw new BadCredentialsException("User is not admin, so he can not delete the flat");
        } else {
            SharedFlat flat = user.getSharedFlat();
            if (flat == null) {
                throw new BadCredentialsException("You can not delete flat where you do not live");
            }
            user.setSharedFlat(null);
            user.setAdmin(false);
            userRepository.save(user);
            boolean exist = userRepository.existsBySharedFlat(flat);

            if (!exist) {
                Long deletedFlatId = flat.getId();
                sharedFlatRepository.deleteById(deletedFlatId);
                return sharedFlatMapper.entityToWgDetailDto(flat);
            } else {
                user.setSharedFlat(flat);
                user.setAdmin(true);
                userRepository.save(user);
                throw new BadCredentialsException("Flat is not empty");
            }
        }

    }
}
