package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDto;
import jakarta.validation.ConstraintViolation;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Set;

@Component
public class RecipeValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Validator validator;


    public RecipeValidator(Validator validator) {
        this.validator = validator;
    }

    public void validateForCreate(RecipeSuggestionDto recipeSuggestionDto) throws ValidationException {
        LOGGER.trace("validateForCreate({})", recipeSuggestionDto);
        this.checkValidationForCreate(recipeSuggestionDto);

    }

    private void checkValidationForCreate(RecipeSuggestionDto recipeSuggestionDto) throws ValidationException {
        LOGGER.trace("checkValidationForCreate({})", recipeSuggestionDto);

        Set<ConstraintViolation<RecipeSuggestionDto>> validationViolations = validator.validate(recipeSuggestionDto);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("The data is not valid: " , validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }
}
