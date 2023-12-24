package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventsRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.EventsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;

public class EventsServiceImpl implements EventsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EventsRepository eventsRepository;

    private final EventMapper eventMapper;

    public EventsServiceImpl(EventsRepository eventsRepository, EventMapper eventMapper) {
        this.eventsRepository = eventsRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    public EventDto create(EventDto event) {
        Event createdEvent = eventsRepository.save(eventMapper.dtoToEntity(event));
        return eventMapper.entityToDto(createdEvent);
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
