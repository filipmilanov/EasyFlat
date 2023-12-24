package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Set;

public class EventValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Validator validator;

    public EventValidator(Validator validator) {
        this.validator = validator;
    }

    public void validateForCreate(EventDto event) throws ValidationException {
        LOGGER.trace("validateForCreate({})", event);

        Set<ConstraintViolation<EventDto>> validationViolations = validator.validate(event);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("Search Data is not valid", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }
}
