package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.DigitalStorageMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemOrderType;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import jakarta.annotation.security.PermitAll;
import jakarta.xml.bind.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<DigitalStorageDto> findAll(DigitalStorageSearchDto digitalStorageDto) {
        LOGGER.info("findAll({})", digitalStorageDto);

        return digitalStorageMapper.entityListToDtoList(
            digitalStorageService.findAll(digitalStorageDto)
        );
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
    public List<Item> getStorageItems(@PathVariable Long id, ItemSearchDto itemSearchDto, ItemOrderType orderType) {
        LOGGER.info("getStorageItems({}, {})", id, itemSearchDto);
        return digitalStorageService.searchItems(id, itemSearchDto, orderType);
    }

    @Secured("ROLE_USER")
    @PatchMapping("{storageId}/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public Item updateItemQuantity(@PathVariable long storageId, @PathVariable long itemId, long quantity) {
        LOGGER.info("updateItemQuantity({}, {}, {})", storageId, itemId, quantity);
        return digitalStorageService.updateItemQuantity(storageId, itemId, quantity);
    }
}
