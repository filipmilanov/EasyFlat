package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ChoreMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChoreRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ChoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class ChoreServiceImpl implements ChoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ChoreRepository choreRepository;

    private final ChoreMapper choreMapper;

    public ChoreServiceImpl(ChoreRepository choreRepository, ChoreMapper choreMapper) {
        this.choreRepository = choreRepository;
        this.choreMapper = choreMapper;
    }

    public ChoreDto createChore(ChoreDto choreDto) {
        Chore chore = choreMapper.choreDtoToEntity(choreDto);
        Chore savedChore = choreRepository.save(chore);
        return choreMapper.entityToChoreDto(savedChore);
    }

    @Override
    public PreferenceDto updatePref(PreferenceDto preference) {
        return null;
    }
}

