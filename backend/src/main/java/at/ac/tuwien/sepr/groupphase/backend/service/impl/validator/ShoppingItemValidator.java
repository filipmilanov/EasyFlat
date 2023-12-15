package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
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
public class ShoppingItemValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Validator validator;

    public ShoppingItemValidator(Validator validator) {
        this.validator = validator;
    }

    public void validateForCreate(ShoppingItemDto itemDto,
                                  List<ShoppingList> shoppingLists,
                                  List<DigitalStorage> digitalStorageList,
                                  List<Unit> unitList) throws ConflictException, ValidationException {
        LOGGER.trace("validateForCreate({})", itemDto);

        checkValidationForCreate(itemDto);
        checkConflictForCreate(itemDto, shoppingLists, digitalStorageList, unitList);
    }

    private void checkValidationForCreate(ShoppingItemDto itemDto) throws ValidationException {
        LOGGER.trace("checkValidationForCreate({})", itemDto);

        Set<ConstraintViolation<ShoppingItemDto>> validationViolations = validator.validate(itemDto);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("The data is not valid", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }

    private void checkConflictForCreate(ShoppingItemDto itemDto, List<ShoppingList> shoppingLists, List<DigitalStorage> digitalStorageList, List<Unit> unitList) throws ConflictException {
        LOGGER.trace("checkItemForCreate({}, {}, {})", itemDto, digitalStorageList, unitList);

        List<String> errors = new ArrayList<>();

        if (itemDto.generalName().isEmpty()) {
            errors.add("Tha name should not be empty!");
        }
        if (itemDto.generalName().length() > 120) {
            errors.add("The name is too long");
        }
        if (itemDto.generalName().isBlank()) {
            errors.add("The given name is blank");
        }
        if (itemDto.itemId() != null) {
            errors.add("The Id must be null");
        }

        if (itemDto.shoppingList() == null) {
            errors.add("The item is not linked to a Shopping List");
        } else {
            if (shoppingLists.stream()
                .map(ShoppingList::getShopListId)
                .noneMatch(id ->
                    Objects.equals(id, itemDto.shoppingList().id())
                )
            ) {
                errors.add("The given Shopping List does not exists");
            }
        }

        if (itemDto.digitalStorage() != null && !itemDto.digitalStorage().title().equals("Storage")) {
            errors.add("The item is linked to to an incorrect Digital Storage");
        }

        if (unitList.stream().map(Unit::getName).noneMatch(name -> name.equals(itemDto.unit().name()))) {
            errors.add("The given Unit does not exists");
        }

        if (!errors.isEmpty()) {
            throw new ConflictException("There is a conflict with persisted data", errors);
        }
    }

    public void validateForUpdate(ShoppingItemDto itemDto,
                                  List<ShoppingList> shoppingLists,
                                  List<DigitalStorage> digitalStorageList,
                                  List<Unit> unitList) throws ConflictException, ValidationException {
        LOGGER.trace("validateForUpdate({})", itemDto);

        checkValidationForUpdate(itemDto);
        checkConflictForUpdate(itemDto, shoppingLists, digitalStorageList, unitList);
    }

    private void checkValidationForUpdate(ShoppingItemDto itemDto) throws ValidationException {
        LOGGER.trace("checkValidationForUpdate({})", itemDto);

        Set<ConstraintViolation<ShoppingItemDto>> validationViolations = validator.validate(itemDto);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("The data is not valid", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }

    private void checkConflictForUpdate(ShoppingItemDto itemDto, List<ShoppingList> shoppingLists, List<DigitalStorage> digitalStorageList, List<Unit> unitList) throws ConflictException {
        LOGGER.trace("checkItemForUpdate({}, {}, {})", itemDto, digitalStorageList, unitList);

        List<String> errors = new ArrayList<>();
        if (itemDto.generalName().isEmpty()) {
            errors.add("Tha name should not be empty!");
        }
        if (itemDto.generalName().length() > 120) {
            errors.add("The name is too long");
        }
        if (itemDto.generalName().isBlank()) {
            errors.add("The given name is blank");
        }
        if (itemDto.itemId() == null) {
            errors.add("The Id can not be null");
        }

        if (itemDto.shoppingList() == null) {
            errors.add("The item is not linked to a Shopping List");
        } else {
            if (shoppingLists.stream()
                .map(ShoppingList::getShopListId)
                .noneMatch(id ->
                    Objects.equals(id, itemDto.shoppingList().id())
                )
            ) {
                errors.add("The given Shopping List does not exists");
            }
        }

        if (itemDto.digitalStorage() != null && !itemDto.digitalStorage().title().equals("Storage")) {
            errors.add("The item is linked to to an incorrect Digital Storage");
        }

        if (itemDto.alwaysInStock() && itemDto.minimumQuantity() == null) {
            errors.add("There is no MinimumQuantity defined");
        }

        if (unitList.stream().map(Unit::getName).noneMatch(name -> name.equals(itemDto.unit().name()))) {
            errors.add("The given Unit does not exists");
        }

        if (!errors.isEmpty()) {
            throw new ConflictException("There is a conflict with persisted data", errors);
        }
    }

}


