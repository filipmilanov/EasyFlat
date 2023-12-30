package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SharedFlatMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.EventLabel;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemLabel;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventsRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.EventLabelService;
import at.ac.tuwien.sepr.groupphase.backend.service.EventsService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.EventValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventsServiceImpl implements EventsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EventsRepository eventsRepository;
    private final EventMapper eventMapper;
    private final AuthService authService;

    private final EventValidator eventValidator;

    private final SharedFlatMapper sharedFlatMapper;
    private final EventLabelService labelService;

    public EventsServiceImpl(EventsRepository eventsRepository, EventMapper eventMapper, AuthService authService, EventValidator eventValidator,
                             SharedFlatMapper sharedFlatMapper, EventLabelService labelService) {
        this.eventsRepository = eventsRepository;
        this.eventMapper = eventMapper;
        this.authService = authService;
        this.eventValidator = eventValidator;
        this.sharedFlatMapper = sharedFlatMapper;
        this.labelService = labelService;
    }

    @Override
    @Transactional
    public EventDto create(EventDto event) throws ValidationException {
        eventValidator.validate(event);
        ApplicationUser user = authService.getUserFromToken();
        List<EventLabel> labels = this.findLabelsAndCreateMissing(event.labels());
        Event toCreate = eventMapper.dtoToEntity(event, labels);
        toCreate.setSharedFlat(user.getSharedFlat());
        Event createdEvent = eventsRepository.save(toCreate);
        return eventMapper.entityToDto(createdEvent, sharedFlatMapper.entityToWgDetailDto(createdEvent.getSharedFlat()));
    }

    @Override
    @Transactional
    public EventDto update(EventDto event) throws AuthorizationException, ValidationException {
        eventValidator.validate(event);
        Optional<Event> existingEventOptional = eventsRepository.findById(event.id());

        if (existingEventOptional.isPresent()) {
            Event existingEvent = existingEventOptional.get();
            ApplicationUser user = authService.getUserFromToken();


            if (user.getSharedFlat().equals(existingEvent.getSharedFlat())) {
                existingEvent.setTitle(event.title());
                existingEvent.setDescription(event.description());
                existingEvent.setDate(event.date());

                if (event.labels() != null) {
                    List<EventLabel> labels = this.findLabelsAndCreateMissing(event.labels());
                    existingEvent.setLabels(labels);
                }

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
    @Transactional
    public EventDto delete(Long id) throws AuthorizationException {
        Optional<Event> existingEventOptional = eventsRepository.findById(id);

        if (existingEventOptional.isPresent()) {
            Event existingEvent = existingEventOptional.get();
            ApplicationUser user = authService.getUserFromToken();

            if (user.getSharedFlat().equals(existingEvent.getSharedFlat())) {
                eventsRepository.delete(existingEvent);

                return eventMapper.entityToDto(existingEvent, sharedFlatMapper.entityToWgDetailDto(existingEvent.getSharedFlat()));
            } else {
                throw new AuthorizationException("User does not have access to delete this event", new ArrayList<>());
            }
        } else {
            throw new EntityNotFoundException("Event not found with id: " + id);
        }
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

    @Override
    public List<EventDto> findEventsByLabel(String labelName) throws AuthorizationException {
        ApplicationUser user = authService.getUserFromToken();

        if (user == null) {
            throw new AuthorizationException("Authorization failed", List.of("User does not exist"));
        }

        Long sharedFlatId = user.getSharedFlat().getId();

        List<Event> events = eventsRepository.findEventsByLabelNameAndSharedFlatId(labelName, sharedFlatId);

        return events.stream()
            .filter(event -> user.getSharedFlat().equals(event.getSharedFlat()))
            .map(event -> eventMapper.entityToDto(event, sharedFlatMapper.entityToWgDetailDto(event.getSharedFlat())))
            .toList();
    }

    @Override
    public String exportAll() {
        ApplicationUser user = authService.getUserFromToken();

        StringBuilder icsContent = new StringBuilder();

        icsContent.append("BEGIN:VCALENDAR\n");
        icsContent.append("VERSION:2.0\n");
        icsContent.append("PRODID:-//EasyFlat//\n");

        List<Event> events = eventsRepository.getBySharedFlatIs(user.getSharedFlat());

        for (Event event : events) {
            icsContent.append("BEGIN:VEVENT\n");

            String uuid = UUID.randomUUID().toString();

            icsContent.append("UID:").append(uuid).append("\n");
            icsContent.append("SUMMARY:").append(event.getTitle()).append("\n");
            icsContent.append("DESCRIPTION:").append(event.getDescription()).append("\n");
            icsContent.append("DTSTART:").append(formatDate(event.getDate())).append("\n");
            icsContent.append("DTEND:").append(formatDate(event.getDate().plusDays(1))).append("\n");
            icsContent.append("END:VEVENT\n");
        }

        icsContent.append("END:VCALENDAR");

        return icsContent.toString();
    }

    private String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return date.format(formatter);
    }


    private List<EventLabel> findLabelsAndCreateMissing(List<EventLabelDto> labels) {
        LOGGER.trace("findLabelsAndCreateMissing({})", labels);
        if (labels == null) {
            return List.of();
        }
        List<String> values = labels.stream()
            .map(EventLabelDto::labelName)
            .toList();
        List<String> colours = labels.stream()
            .map(EventLabelDto::labelColour)
            .toList();

        List<EventLabel> ret = new ArrayList<>();
        if (!values.isEmpty()) {
            for (int i = 0; i < values.size(); i++) {
                EventLabel found = labelService.findByValueAndColour(values.get(i), colours.get(i));
                if (found != null) {
                    ret.add(found);
                }
            }
        }

        List<EventLabelDto> missingLabels = labels.stream()
            .filter(labelDto ->
                ret.stream()
                    .noneMatch(label ->
                        (label.getLabelName().equals(labelDto.labelName())
                            && label.getLabelColour().equals(labelDto.labelColour()))
                    )
            ).toList();

        if (!missingLabels.isEmpty()) {
            List<EventLabel> createdLabels = labelService.createAll(missingLabels);
            ret.addAll(createdLabels);
        }
        return ret;
    }
}
