package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/storage")
public class StorageEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final DigitalStorageService digitalStorageService;

    public StorageEndpoint(DigitalStorageService digitalStorageService) {
        this.digitalStorageService = digitalStorageService;
    }

    @Secured("ROLE_USER")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DigitalStorage create(@RequestBody DigitalStorageDto digitalStorageDto) {
        LOGGER.info("create({})", digitalStorageDto);
        return digitalStorageService.create(digitalStorageDto);
    }


    @Secured("ROLE_USER")
    @GetMapping("{id}")
    public List<Item> getAllItems(@PathVariable Long id) {
        return digitalStorageService.findAllItemsOfStorage(id);
    }
}
