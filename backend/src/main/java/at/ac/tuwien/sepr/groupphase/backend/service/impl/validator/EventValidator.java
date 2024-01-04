package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
public class EventValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Validator validator;

    public EventValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(EventDto event) throws ValidationException {
        LOGGER.trace("validateForCreate({})", event);

        autoCheck(event);

    }


    private void autoCheck(EventDto event) throws ValidationException {
        Set<ConstraintViolation<EventDto>> validationViolations = validator.validate(event);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("Data is not valid", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }


}
