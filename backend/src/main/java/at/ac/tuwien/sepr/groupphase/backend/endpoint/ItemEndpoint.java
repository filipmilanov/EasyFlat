package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemFromApiDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

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
        return itemMapper.entityToDto(
            itemService.create(itemDto)
        );
    }

    @Secured("ROLE_USER")
    @GetMapping("{itemId}")
    public Optional<ItemDto> findById(@PathVariable Long itemId) {
        LOGGER.info("findById({})", itemId);
        Optional<Item> item = itemService.findById(itemId);

        return item.flatMap(currentItem -> Optional.ofNullable(itemMapper.entityToDto(currentItem)));
    }

    @Secured("ROLE_USER")
    @PutMapping("{id}")
    public ItemDto update(@PathVariable long id, @RequestBody ItemDto itemDto) throws ValidationException, ConflictException {
        LOGGER.info("update({},{})", id, itemDto);
        return itemMapper.entityToDto(
            itemService.update(itemDto.withId(id))
        );
    }

    @Secured("ROLE_USER")
    @DeleteMapping("{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long itemId) {
        LOGGER.info("delete({})", itemId);
        itemService.delete(itemId);
    }

    @Secured("ROLE_USER")
    @GetMapping("/ean/{ean}")
    public ItemFromApiDto findItemByEan(@PathVariable Long ean) {
        LOGGER.info("findItemByEan({})", ean);
        return itemService.findItemByEan(ean);
    }

}
