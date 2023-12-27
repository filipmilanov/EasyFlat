package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ChoreMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChoreRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.ChoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class ChoreServiceImpl implements ChoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ChoreRepository choreRepository;

    private final ChoreMapper choreMapper;

    private final AuthService authService;

    public ChoreServiceImpl(ChoreRepository choreRepository, ChoreMapper choreMapper, AuthService authService) {
        this.choreRepository = choreRepository;
        this.choreMapper = choreMapper;
        this.authService = authService;
    }

    @Secured("ROLE_USER")
    public ChoreDto createChore(ChoreDto choreDto) throws AuthenticationException {
        ApplicationUser applicationUser = authService.getUserFromToken();
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exist"));
        }
        Chore chore = choreMapper.choreDtoToEntity(choreDto);
        Chore savedChore = choreRepository.save(chore);
        return choreMapper.entityToChoreDto(savedChore);
    }

    @Override
    @Secured("ROLE_USER")
    public List<Chore> getChores(String searchParams) throws AuthenticationException {
        ApplicationUser applicationUser = authService.getUserFromToken();
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exist"));
        }
        return choreRepository.findAll();
    }


    @Override
    public PreferenceDto updatePref(PreferenceDto preference) {
        return null;
    }
}

