package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

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
import java.util.Set;

@Component
public class ShoppingListValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final Validator validator;

    public ShoppingListValidator(Validator validator) {
        this.validator = validator;
    }

    public void validateForCreate(ShoppingList shoppingList) throws ConflictException, ValidationException {
        LOGGER.trace("validateForCreate({})", shoppingList);
        checkValidationForCreate(shoppingList);
        checkConflictForCreate(shoppingList);
    }

    private void checkValidationForCreate(ShoppingList shoppingList) throws ValidationException {
        LOGGER.trace("checkValidationForCreate({})", shoppingList);
        Set<ConstraintViolation<ShoppingList>> validationViolations = validator.validate(shoppingList);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("The data is not valid", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }

    private void checkConflictForCreate(ShoppingList shoppingList) throws ConflictException {
        LOGGER.trace("checkConflictForCreate({})", shoppingList);
        List<String> errors = new ArrayList<>();

        if (shoppingList.getName() == "Default") {
            errors.add("Shopping List can not have 'Default' name");
        }

        if (shoppingList.getName() == null || shoppingList.getName().trim().isEmpty()) {
            errors.add("Shopping List name must not be null or empty");
        }
        if (shoppingList.getName().isBlank()) {
            errors.add("Shopping List name can not be blank");
        }
        if (shoppingList.getName().length() > 120) {
            errors.add("Shopping List name is too long");
        }

        if (!errors.isEmpty()) {
            throw new ConflictException("There is a conflict with persisted data", errors);
        }
    }

}
