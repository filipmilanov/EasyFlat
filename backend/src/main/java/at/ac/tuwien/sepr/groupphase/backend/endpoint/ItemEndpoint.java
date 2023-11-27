package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemService;
import jakarta.xml.bind.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
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
    private final ItemMapper itemMapper;

    public ItemEndpoint(ItemService itemService, ItemMapper itemMapper) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }

    @Secured("ROLE_USER")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestBody ItemDto itemDto) throws ValidationException, ConflictException {
        LOGGER.info("create({})", itemDto);
        return itemMapper.itemToDto(
            itemService.create(itemDto)
        );
    }

}
