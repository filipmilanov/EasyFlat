package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.service.EventsService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping("/api/v1/events")
public class EventsEndPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EventsService eventsService;

    public EventsEndPoint(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @PermitAll
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EventDto create(@RequestBody EventDto event) {
        return eventsService.create(event);
    }

    @Secured("ROLE_USER")
    @PutMapping
    public EventDto update(EventDto event) {
        return eventsService.create(event);
    }

    @Secured("ROLE_USER")
    @DeleteMapping
    public EventDto delete(EventDto event) {
        return eventsService.create(event);
    }

}
