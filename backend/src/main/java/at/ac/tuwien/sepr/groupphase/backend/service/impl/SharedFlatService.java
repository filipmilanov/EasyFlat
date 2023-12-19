package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SharedFlatMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cookbook;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.CookbookRepository;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.DigitalStorageRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SharedFlatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.authenticator.Authorization;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.SharedFlatValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
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
    private final CookbookRepository cookbookRepository;

    private final SharedFlatValidator validator;

    @Autowired
    public SharedFlatService(SharedFlatRepository sharedFlatRepository,
                             PasswordEncoder passwordEncoder,
                             SharedFlatMapper sharedFlatMapper,
                             DigitalStorageRepository digitalStorageRepository,
                             CookbookRepository cookbookRepository,
                             JwtTokenizer jwtTokenizer,
                             UserRepository userRepository,
                             Authorization authorization,
                             ShoppingListRepository shoppingListRepository,
                             SharedFlatValidator validator) {
        this.sharedFlatRepository = sharedFlatRepository;
        this.passwordEncoder = passwordEncoder;
        this.sharedFlatMapper = sharedFlatMapper;
        this.jwtTokenizer = jwtTokenizer;
        this.userRepository = userRepository;
        this.authorization = authorization;
        this.digitalStorageRepository = digitalStorageRepository;


        this.cookbookRepository = cookbookRepository;
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

    @Transactional
    public WgDetailDto create(SharedFlat sharedFlat, ApplicationUser user) throws ConflictException, ValidationException {
        LOGGER.trace("create({}, {})", sharedFlat, user);
        LOGGER.debug("Create a new shared flat");
        validator.validateForCreate(sharedFlat);
        SharedFlat existingSharedFlat = sharedFlatRepository.findFirstByName(sharedFlat.getName());
        if (existingSharedFlat != null) {
            throw new ConflictException("A flat with this name already exists.");
        }
        SharedFlat newSharedFlat = new SharedFlat();
        newSharedFlat.setName(sharedFlat.getName());
        newSharedFlat.setPassword(passwordEncoder.encode(sharedFlat.getPassword()));
        sharedFlatRepository.save(newSharedFlat);
        user.setSharedFlat(newSharedFlat);
        user.setAdmin(true);
        userRepository.save(user);
        DigitalStorage digitalStorage = new DigitalStorage();
        digitalStorage.setTitle("Storage " + newSharedFlat.getName());
        digitalStorage.setSharedFlat(newSharedFlat);
        newSharedFlat.setDigitalStorage(digitalStorage);

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setName("Default");
        shoppingList.setSharedFlat(newSharedFlat);
        shoppingListRepository.save(shoppingList);

        digitalStorageRepository.save(digitalStorage);

        Cookbook cookbook = new Cookbook();
        cookbook.setTitle("Cookbook " + newSharedFlat.getName());
        cookbook.setSharedFlat(newSharedFlat);
        newSharedFlat.setCookbook(cookbook);

        cookbookRepository.save(cookbook);

        return sharedFlatMapper.entityToWgDetailDto(newSharedFlat);
    }

    @Override
    @Transactional
    public WgDetailDto loginWg(SharedFlat wgDetailDto, ApplicationUser user) {
        LOGGER.trace("loginWg({}, {})", wgDetailDto, user);
        SharedFlat existingSharedFlat = sharedFlatRepository.findFirstByName(wgDetailDto.getName());
        if (existingSharedFlat != null) {
            boolean passwordMatches = passwordEncoder.matches(wgDetailDto.getPassword(), existingSharedFlat.getPassword());
            if (passwordMatches) {
                if (user.getEmail() != null) {
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


    @Transactional
    public WgDetailDto delete(ApplicationUser user) throws AuthorizationException {
        LOGGER.trace("delete({})", user);
        if (!user.getAdmin()) {
            throw new AuthorizationException("Authorization failed", List.of("Only the admin can delete the flat"));
        } else {
            SharedFlat flat = user.getSharedFlat();
            if (flat == null) {
                throw new AuthorizationException("Authorization failed", List.of("You can not delete flat where you do not live"));
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
                throw new AuthorizationException("Authorization failed", List.of("You can not delete flat which is not empty"));
            }
        }

    }
}
