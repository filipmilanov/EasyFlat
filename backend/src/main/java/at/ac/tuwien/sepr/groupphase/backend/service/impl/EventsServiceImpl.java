package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SharedFlatMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventsRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.EventsService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.EventValidator;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public EventDto update(EventDto event) throws AuthorizationException {
        Optional<Event> existingEventOptional = eventsRepository.findById(event.id());

        if (existingEventOptional.isPresent()) {
            Event existingEvent = existingEventOptional.get();
            ApplicationUser user = authService.getUserFromToken();


            if (user.getSharedFlat().equals(existingEvent.getSharedFlat())) {
                existingEvent.setTitle(event.title());
                existingEvent.setDescription(event.description());
                existingEvent.setDate(event.date());

                Event updatedEvent = eventsRepository.save(existingEvent);

                return eventMapper.entityToDto(updatedEvent, sharedFlatMapper.entityToWgDetailDto(updatedEvent.getSharedFlat()));
            } else {

                throw new AuthorizationException("User does not have access to update this event", new ArrayList<>());
            }
        } else {

            throw new EntityNotFoundException("Event not found with id: " + event.id());
        }
    }

    @Override
    public EventDto delete(EventDto event) {
        return null;
    }

    @Override
    public List<EventDto> findAll() {
        ApplicationUser user = authService.getUserFromToken();

        return eventsRepository.getBySharedFlatIs(user.getSharedFlat()).stream().map(event -> eventMapper.entityToDto(event, sharedFlatMapper.entityToWgDetailDto(user.getSharedFlat()))).toList();
    }

    @Override
    public EventDto getEventWithId(Long id) throws AuthorizationException {
        Optional<Event> eventOptional = eventsRepository.findById(id);

        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            ApplicationUser user = authService.getUserFromToken();
            if (user.getSharedFlat().equals(event.getSharedFlat())) {

                return eventMapper.entityToDto(event, sharedFlatMapper.entityToWgDetailDto(event.getSharedFlat()));
            } else {

                throw new AuthorizationException("User does not have access to this event", new ArrayList<String>());
            }
        } else {
            throw new EntityNotFoundException("Event not found with id: " + id);
        }
    }
}
