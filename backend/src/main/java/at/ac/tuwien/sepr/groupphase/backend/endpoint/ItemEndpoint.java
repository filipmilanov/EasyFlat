package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemService;
import jakarta.xml.bind.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/item")
public class ItemEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ItemService itemService;

    public ItemEndpoint(ItemService itemService) {
        this.itemService = itemService;
    }

    @Secured("ROLE_USER")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item create(@RequestBody ItemDto itemDto) throws ValidationException, ConflictException {
        LOGGER.info("create({})", itemDto);
        return itemService.create(itemDto);
    }

    @Secured("ROLE_USER")
    @PutMapping
    public Item update(@RequestBody ItemDto itemDto) throws ConflictException {
        LOGGER.info("update({})", itemDto);
        return itemService.update(itemDto);
    }

}
