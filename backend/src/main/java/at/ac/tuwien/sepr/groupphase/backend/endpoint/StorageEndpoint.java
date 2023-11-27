package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.DigitalStorageMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import jakarta.annotation.security.PermitAll;
import jakarta.xml.bind.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/storage")
public class StorageEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final DigitalStorageService digitalStorageService;
    private final DigitalStorageMapper digitalStorageMapper;

    public StorageEndpoint(DigitalStorageService digitalStorageService, DigitalStorageMapper digitalStorageMapper) {
        this.digitalStorageService = digitalStorageService;
        this.digitalStorageMapper = digitalStorageMapper;
    }

    @Secured("ROLE_USER")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DigitalStorageDto create(@RequestBody DigitalStorageDto digitalStorageDto) throws ValidationException, ConflictException {
        LOGGER.info("create({})", digitalStorageDto);
        return digitalStorageMapper.entityToDto(
            digitalStorageService.create(digitalStorageDto)
        );
    }

    @PermitAll
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<Item> getAllItems(@PathVariable Long id) {
        LOGGER.info("getAllItems({})", id);
        List<Item> test = digitalStorageService.findAllItemsOfStorage(id);
        return test;
    }
}
