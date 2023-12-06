package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SharedFlatMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.repository.SharedFlatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class SharedFlatService implements at.ac.tuwien.sepr.groupphase.backend.service.SharedFlatService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SharedFlatRepository sharedFlatRepository;
    private final PasswordEncoder passwordEncoder;
    private final SharedFlatMapper sharedFlatMapper;

    private final UserRepository userRepository;

    private final JwtTokenizer jwtTokenizer;

    @Autowired
    public SharedFlatService(SharedFlatRepository sharedFlatRepository, PasswordEncoder passwordEncoder, SharedFlatMapper sharedFlatMapper, JwtTokenizer jwtTokenizer, UserRepository userRepository) {
        this.sharedFlatRepository = sharedFlatRepository;
        this.passwordEncoder = passwordEncoder;
        this.sharedFlatMapper = sharedFlatMapper;
        this.jwtTokenizer = jwtTokenizer;
        this.userRepository = userRepository;
    }


    public WgDetailDto create(SharedFlat sharedFlat, String authToken) throws Exception {
        LOGGER.debug("Create a new shared flat");
        SharedFlat existingSharedFlat = sharedFlatRepository.findFirstByName(sharedFlat.getName());
        if (existingSharedFlat != null) {
            throw new Exception("A flat with this name already exists.");
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
        return sharedFlatMapper.entityToWgDetailDto(newSharedFlat);
    }

    @Override
    public WgDetailDto loginWg(SharedFlat wgDetailDto, String authToken) {
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
