package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SharedFlatMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.repository.SharedFlatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
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
            throw new Exception("This name already exists!");
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
    public WgDetailDto delete(String name) {
        SharedFlat flat = sharedFlatRepository.findFlatByName(name);
        boolean exist = userRepository.existsBySharedFlat(flat);
        if (flat == null) {
            throw new BadCredentialsException("There is no flat with this name");
        }
        if (!exist) {
            SharedFlat deletedFlat = sharedFlatRepository.findFirstByName(name);
            sharedFlatRepository.deleteByName(name);
            return sharedFlatMapper.entityToWgDetailDto(deletedFlat);
        }
        throw new BadCredentialsException("Flat is not empty");
    }


}
