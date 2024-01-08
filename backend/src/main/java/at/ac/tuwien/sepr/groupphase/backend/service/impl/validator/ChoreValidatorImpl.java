package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.interfaces.ChoreValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class ChoreValidatorImpl implements ChoreValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Validator validator;

    public ChoreValidatorImpl(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void validateForCreate(ChoreDto chore) throws ValidationException, ConflictException {
        LOGGER.trace("validateForCreate({})", chore);

        checkValidationForCreate(chore);
        checkConflictForCreate(chore);
    }

    private void checkConflictForCreate(ChoreDto chore) throws ConflictException {
        LOGGER.trace("checkConflictForCreate({})", chore);

        List<String> errors = new ArrayList<>();

        if (chore.choreName() == null) {
            errors.add("No name given");
        } else {
            if (chore.choreName().isBlank()) {
                errors.add("The given name can not be blank");
            }
            if (chore.choreName().length() > 120) {
                errors.add("The name is too long");
            }
        }
        if (chore.points() < 0) {
            errors.add("You can't give negative points to a chore");
        }
        if (chore.endDate().after(new Date())) {
            errors.add("You can not create a chore with expired date");
        }
        if (chore.description().isBlank()) {
            errors.add("The description can not be blank");
        }

        if (!errors.isEmpty()) {
            throw new ConflictException("There is a conflict with persisted data", errors);
        }
    }

    private void checkValidationForCreate(ChoreDto chore) throws ValidationException {
        LOGGER.trace("checkValidationForCreate({})", chore);
        Set<ConstraintViolation<ChoreDto>> validationViolations = validator.validate(chore);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("The data is not valid", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }
}
