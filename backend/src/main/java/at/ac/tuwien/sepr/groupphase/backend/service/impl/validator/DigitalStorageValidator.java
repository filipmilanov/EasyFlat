package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Component
public class DigitalStorageValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    public void checkDigitalStorageForCreate(DigitalStorageDto digitalStorageDto) throws ConflictException {
        LOGGER.trace("checkDigitalStorageForCreate({})", digitalStorageDto);

        if (digitalStorageDto.storId() != null) {
            throw new ConflictException("Conflict with other data", List.of("The Id must be null"));
        }
    }
}
