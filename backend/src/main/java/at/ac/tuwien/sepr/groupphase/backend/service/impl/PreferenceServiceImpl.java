package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PreferenceMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChoreRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PreferenceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.PreferenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Service
public class PreferenceServiceImpl implements PreferenceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final AuthService authService;

    private final PreferenceRepository preferenceRepository;

    private final PreferenceMapper preferenceMapper;

    private final ChoreRepository choreRepository;

    private final UserRepository userRepository;


    public PreferenceServiceImpl(AuthService authService, PreferenceRepository preferenceRepository, PreferenceMapper preferenceMapper, ChoreRepository choreRepository, UserRepository userRepository) {
        this.authService = authService;
        this.preferenceRepository = preferenceRepository;
        this.preferenceMapper = preferenceMapper;
        this.choreRepository = choreRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PreferenceDto update(PreferenceDto preferenceDto) throws AuthenticationException {
        LOGGER.trace("update({})", preferenceDto);
        ApplicationUser applicationUser = authService.getUserFromToken();
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exist"));
        }


        Preference preference = preferenceMapper.preferenceDtoToEntity(preferenceDto);
        Chore firstChore = getChoreFromName(preferenceDto.first());
        if (firstChore != null) {
            preference.setFirstId(firstChore.getId());
        }
        Chore secondChore = getChoreFromName(preferenceDto.second());
        if (secondChore != null) {
            preference.setSecondId(secondChore.getId());
        }
        Chore thirdChore = getChoreFromName(preferenceDto.third());
        if (thirdChore != null) {
            preference.setThirdId(thirdChore.getId());
        }
        Chore fourthChore = getChoreFromName(preferenceDto.fourth());
        if (fourthChore != null) {
            preference.setFourthId(fourthChore.getId());
        }
        preference.setUserId(applicationUser);

        Optional<Preference> existingPreference = Optional.ofNullable(preferenceRepository.findByUserId(applicationUser));

        if (existingPreference.isPresent()) {
            Preference existing = existingPreference.get();
            existing.setFirstId(preference.getFirstId());
            existing.setSecondId(preference.getSecondId());
            existing.setThirdId(preference.getThirdId());
            existing.setFourthId(preference.getFourthId());

            Preference toSave = preferenceRepository.save(existing);
            applicationUser.setPreference(existing);
            userRepository.save(applicationUser);
            return preferenceMapper.entityToPreferenceDto(toSave);
        } else {
            Preference toSave = preferenceRepository.save(preference);
            applicationUser.setPreference(preference);
            userRepository.save(applicationUser);
            return preferenceMapper.entityToPreferenceDto(toSave);
        }
    }

    @Override
    public PreferenceDto getLastPreference() throws AuthenticationException {
        LOGGER.trace("getLastPreference()");
        ApplicationUser applicationUser = authService.getUserFromToken();
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exist"));
        }
        Preference preferenceToRet = preferenceRepository.findByUserId(applicationUser);
        if (preferenceToRet != null) {
            return preferenceMapper.entityToPreferenceDto(preferenceToRet);
        } else {
            return null;
        }
    }



    private Chore getChoreFromName(String choreName) {
        return choreRepository.findByName(choreName);
    }
}
