package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SharedFlatMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.repository.SharedFlatRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class SharedFlatService implements at.ac.tuwien.sepr.groupphase.backend.service.SharedFlatService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SharedFlatRepository sharedFlatRepository;
    private final PasswordEncoder passwordEncoder;
    private final SharedFlatMapper sharedFlatMapper;

    @Autowired
    public SharedFlatService(SharedFlatRepository sharedFlatRepository, PasswordEncoder passwordEncoder, SharedFlatMapper sharedFlatMapper) {
        this.sharedFlatRepository = sharedFlatRepository;
        this.passwordEncoder = passwordEncoder;
        this.sharedFlatMapper = sharedFlatMapper;
    }


    @Override
    public WgDetailDto create(SharedFlat sharedFlat) {
        LOGGER.debug("Create a new shared flat");
        SharedFlat newSharedFlat = new SharedFlat();
        newSharedFlat.setName(sharedFlat.getName());
        newSharedFlat.setPassword(passwordEncoder.encode(sharedFlat.getPassword()));
        sharedFlatRepository.save(newSharedFlat);
        return sharedFlatMapper.entityToWgDetailDto(newSharedFlat);
    }

    @Override
    public WgDetailDto loginWg(WgDetailDto wgDetailDto) {
        String name = wgDetailDto.getName();
        String rawPassword = wgDetailDto.getPassword();

        // Fetch the stored SharedFlat by name from the database
        SharedFlat existingSharedFlat = sharedFlatRepository.findByName(name);

        if (existingSharedFlat != null) {
            //Compare the raw password with the stored encoded password
            boolean passwordMatches = passwordEncoder.matches(rawPassword, existingSharedFlat.getPassword());

            if (passwordMatches) {
                return sharedFlatMapper.entityToWgDetailDto(existingSharedFlat);
            } else {
                throw new IllegalStateException("Invalid credentials. Could not log in.");
            }
        } else {
            throw new IllegalStateException("Invalid credentials. Could not log in.");
        }
    }

    @Override
    public void deleteByName(String name) {
        sharedFlatRepository.deleteByName(name);
    }


}
