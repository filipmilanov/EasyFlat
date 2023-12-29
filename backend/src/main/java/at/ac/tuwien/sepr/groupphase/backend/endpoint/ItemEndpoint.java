package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemFieldSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemService;
import at.ac.tuwien.sepr.groupphase.backend.service.OpenFoodFactsService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/item")
public class ItemEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ItemService itemService;
    private final OpenFoodFactsService openFoodFactsService;
    private final ItemMapper itemMapper;

    public ItemEndpoint(ItemService itemService, OpenFoodFactsService openFoodFactsService, ItemMapper itemMapper) {
        this.itemService = itemService;
        this.openFoodFactsService = openFoodFactsService;
        this.itemMapper = itemMapper;
    }

    @Secured("ROLE_USER")
    @GetMapping("{itemId}")
    public ItemDto findById(@PathVariable Long itemId, @RequestHeader("Authorization") String jwt) throws AuthorizationException {
        LOGGER.info("findById({})", itemId);

        return itemMapper.entityToDto(
            itemService.findById(itemId, jwt)
        );
    }

    @Secured("ROLE_USER")
    @GetMapping("/ean/{ean}")
    public ItemDto findByEan(@PathVariable Long ean) throws ConflictException, JsonProcessingException {
        LOGGER.info("findByEan({})", ean);
        return itemMapper.openFoodFactItemDtoToItemDto(
            openFoodFactsService.findByEan(ean)
        );
    }

    @Secured("ROLE_USER")
    @GetMapping("search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> findByFields(ItemFieldSearchDto itemFieldSearchDto) {
        LOGGER.info("findByFields({})", itemFieldSearchDto);
        return itemMapper.entityListToItemDtoList(
            itemService.findByFields(itemFieldSearchDto)
        );
    }

    @Secured("ROLE_USER")
    @GetMapping("/general-name/{generalName}")
    public List<ItemDto> findByGeneralName(@PathVariable("generalName") String name, @RequestHeader("Authorization") String jwt) {
        LOGGER.info("findByGeneralName({})", name);
        return itemMapper.entityListToItemDtoList(
            itemService.getItemWithGeneralName(name, jwt)
        );
    }

    @Secured("ROLE_USER")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestBody ItemDto itemDto, @RequestHeader("Authorization") String jwt) throws ValidationException, ConflictException, AuthorizationException {
        LOGGER.info("create({})", itemDto);
        return itemMapper.entityToDto(
            itemService.create(itemDto, jwt)
        );
    }

    @Secured("ROLE_USER")
    @PutMapping("{id}")
    public ItemDto update(@PathVariable long id, @RequestBody ItemDto itemDto, @RequestHeader("Authorization") String jwt) throws ValidationException, ConflictException, AuthorizationException {
        LOGGER.info("update({},{})", id, itemDto);
        return itemMapper.entityToDto(
            itemService.update(itemDto.withId(id), jwt)
        );
    }

    @Secured("ROLE_USER")
    @DeleteMapping("{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long itemId, @RequestHeader("Authorization") String jwt) throws AuthorizationException {
        LOGGER.info("delete({})", itemId);
        itemService.delete(itemId, jwt);
    }

}
