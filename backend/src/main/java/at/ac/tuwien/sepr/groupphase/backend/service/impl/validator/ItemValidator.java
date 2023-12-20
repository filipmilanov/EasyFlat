package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
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
public class ItemValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Validator validator;

    public ItemValidator(Validator validator) {
        this.validator = validator;
    }

    public void validateForCreate(ItemDto itemDto,
                                  List<DigitalStorage> digitalStorageList,
                                  List<Unit> unitList) throws ConflictException, ValidationException {
        LOGGER.trace("validateForCreate({})", itemDto);

        checkValidationForCreate(itemDto);
        checkConflictForCreate(itemDto, digitalStorageList, unitList);
    }

    private void checkValidationForCreate(ItemDto itemDto) throws ValidationException {
        LOGGER.trace("checkValidationForCreate({})", itemDto);

        Set<ConstraintViolation<ItemDto>> validationViolations = validator.validate(itemDto);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("The data is not valid", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }

    private void checkConflictForCreate(ItemDto itemDto,
                                        List<DigitalStorage> digitalStorageList,
                                        List<Unit> unitList) throws ConflictException {
        LOGGER.trace("checkItemForCreate({}, {})", itemDto, digitalStorageList);

        List<String> errors = new ArrayList<>();
        if (itemDto.itemId() != null) {
            errors.add("The Id must be null");
        }

        if (itemDto.digitalStorage() == null || itemDto.digitalStorage().storId() == null) {
            errors.add("There is no Digital Storage defined");
        } else if (digitalStorageList == null
            || digitalStorageList.stream()
            .map(DigitalStorage::getStorId)
            .noneMatch(id ->
                Objects.equals(id, itemDto.digitalStorage().storId())
            )
        ) {
            errors.add("The given Digital Storage does not exists");
        }

        if (itemDto.alwaysInStock() == null) {
            errors.add("There is no AlwaysInStock defined");
        } else if (itemDto.alwaysInStock() && itemDto.minimumQuantity() == null) {
            errors.add("There is no MinimumQuantity defined");
        }

        if (unitList.stream().map(Unit::getName).noneMatch(name -> name.equals(itemDto.unit().name()))) {
            errors.add("The given Unit does not exists");
        }

        if (!errors.isEmpty()) {
            throw new ConflictException("There is a conflict with persisted data", errors);
        }
    }

    public void validateForUpdate(ItemDto itemDto,
                                  List<DigitalStorage> digitalStorageList,
                                  List<Unit> unitList) throws ConflictException, ValidationException {
        LOGGER.trace("validateForUpdate({})", itemDto);

        checkValidationForUpdate(itemDto);
        checkConflictForUpdate(itemDto, digitalStorageList, unitList);
    }

    private void checkValidationForUpdate(ItemDto itemDto) throws ValidationException {
        LOGGER.trace("checkValidationForUpdate({})", itemDto);

        Set<ConstraintViolation<ItemDto>> validationViolations = validator.validate(itemDto);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("The data is not valid", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }

    public void checkConflictForUpdate(ItemDto itemDto,
                                       List<DigitalStorage> digitalStorageList,
                                       List<Unit> unitList) throws ConflictException {
        LOGGER.trace("checkConflictForUpdate({}, {})", itemDto, digitalStorageList);

        List<String> errors = new ArrayList<>();
        if (itemDto.itemId() == null) {
            errors.add("The item id can't be null");
        }

        if (itemDto.digitalStorage() == null || itemDto.digitalStorage().storId() == null) {
            errors.add("There is no Digital Storage defined");
        } else if (digitalStorageList == null
            || digitalStorageList.stream()
            .map(DigitalStorage::getStorId)
            .noneMatch(id ->
                Objects.equals(id, itemDto.digitalStorage().storId())
            )
        ) {
            errors.add("The given Digital Storage does not exist");
        }

        if (itemDto.alwaysInStock() == null) {
            errors.add("There is no AlwaysInStock defined");
        } else if (itemDto.alwaysInStock() && itemDto.minimumQuantity() == null) {
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
