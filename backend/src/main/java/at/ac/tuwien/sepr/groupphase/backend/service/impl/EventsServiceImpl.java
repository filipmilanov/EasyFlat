package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SharedFlatMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventsRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.EventsService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.EventValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class EventsServiceImpl implements EventsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EventsRepository eventsRepository;
    private final EventMapper eventMapper;
    private final AuthService authService;

    private final EventValidator eventValidator;

    private final SharedFlatMapper sharedFlatMapper;

    public EventsServiceImpl(EventsRepository eventsRepository, EventMapper eventMapper, AuthService authService, EventValidator eventValidator, SharedFlatMapper sharedFlatMapper) {
        this.eventsRepository = eventsRepository;
        this.eventMapper = eventMapper;
        this.authService = authService;
        this.eventValidator = eventValidator;
        this.sharedFlatMapper = sharedFlatMapper;
    }

    @Override
    public EventDto create(EventDto event) throws ValidationException {
        eventValidator.validateForCreate(event);
        ApplicationUser user = authService.getUserFromToken();
        Event toCreate = eventMapper.dtoToEntity(event);
        toCreate.setSharedFlat(user.getSharedFlat());
        Event createdEvent = eventsRepository.save(toCreate);
        return eventMapper.entityToDto(createdEvent, sharedFlatMapper.entityToWgDetailDto(createdEvent.getSharedFlat()));
    }

    @Override
    public EventDto update(EventDto event) {
        return null;
    }

    @Override
    public EventDto delete(EventDto event) {
        return null;
    }

    @Override
    public List<EventDto> findAll() {
        return null;
    }
}
