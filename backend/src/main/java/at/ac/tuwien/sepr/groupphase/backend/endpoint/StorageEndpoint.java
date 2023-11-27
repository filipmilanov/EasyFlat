package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
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

    public StorageEndpoint(DigitalStorageService digitalStorageService) {
        this.digitalStorageService = digitalStorageService;
    }

    @Secured("ROLE_USER")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DigitalStorage create(@RequestBody DigitalStorageDto digitalStorageDto) throws ValidationException, ConflictException {
        LOGGER.info("create({})", digitalStorageDto);
        return digitalStorageService.create(digitalStorageDto);
    }

    @PermitAll
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<Item> getStorageItems(@PathVariable Long id,ItemSearchDto itemSearchDto) {
        LOGGER.info("getStorageItems({})", id);
        return digitalStorageService.searchItems(id,itemSearchDto);
    }
}
