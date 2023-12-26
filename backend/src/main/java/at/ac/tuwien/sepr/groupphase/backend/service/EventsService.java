package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface EventsService {

    EventDto create(EventDto event) throws ValidationException;

    EventDto update(EventDto event) throws AuthorizationException;

    EventDto delete(EventDto event);

    List<EventDto> findAll();

    EventDto getEventWithId(Long id) throws AuthorizationException;
}
