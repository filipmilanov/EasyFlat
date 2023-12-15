package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
public class SharedFlatValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Validator validator;

    public SharedFlatValidator(Validator validator) {
        this.validator = validator;
    }

    public void validateForCreate(SharedFlat sharedFlat) throws ConflictException, ValidationException {
        LOGGER.trace("validateForCreate({})", sharedFlat);

        checkValidationForCreate(sharedFlat);
        checkConflictForCreate(sharedFlat);
    }

    private void checkValidationForCreate(SharedFlat sharedFlat) throws ValidationException {
        LOGGER.trace("checkValidationForCreate({})", sharedFlat);

        Set<ConstraintViolation<SharedFlat>> validationViolations = validator.validate(sharedFlat);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("The data is not valid", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }

    private void checkConflictForCreate(SharedFlat sharedFlat) throws ConflictException {
        LOGGER.trace("checkConflictForCreate({})", sharedFlat);

        List<String> errors = new ArrayList<>();

        if (sharedFlat.getName().isEmpty()) {
            errors.add("Shared Flat name should not be empty!");
        }
        if (sharedFlat.getName().length() > 120) {
            errors.add("Shared Flat is too long");
        }
        if (sharedFlat.getName().isBlank()) {
            errors.add("Shared Flat name is blank");
        }
        if (sharedFlat.getName().contains(" ")) {
            errors.add("Shared Flat name should not contain whitespaces");
        }
        if (sharedFlat.getPassword() == null) {
            errors.add("Password is not given");
        }
        if (sharedFlat.getPassword().length() < 8) {
            errors.add("Password is too short");
        }

        if (!errors.isEmpty()) {
            throw new ConflictException("There is a conflict with persisted data", errors);
        }
    }

}
