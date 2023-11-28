package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ItemValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    public void checkItemForCreate(ItemDto itemDto,
                                   List<DigitalStorage> digitalStorageList) throws ConflictException {
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
        }

        if (!errors.isEmpty()) {
            throw new ConflictException("There is a conflict with persisted data", errors);
        }
    }

    public void checkItemForUpdate(ItemDto itemDto,
                                   DigitalStorage digitalStorage) throws ConflictException {
        LOGGER.trace("checkItemForUpdate({}, {})", itemDto, digitalStorage);

        List<String> errors = new ArrayList<>();
        if (itemDto.itemId() == null) {
            errors.add("The item id can't be null");
        }

        if (itemDto.digitalStorage() == null) {
            errors.add("There is no Digital Storage defined for this item");
        } else if (digitalStorage == null || !Objects.equals(digitalStorage.getStorId(), itemDto.digitalStorage())) {
            errors.add("The given Digital Storage does not exists");
        }

        if (!errors.isEmpty()) {
            throw new ConflictException("There is a conflict with persisted data", errors);
        }
    }
}
