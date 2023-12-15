package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.DigitalStorageMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShoppingListMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
    private final ItemMapper itemMapper;
    private final ShoppingListMapper shoppingListMapper;

    public StorageEndpoint(DigitalStorageService digitalStorageService, DigitalStorageMapper digitalStorageMapper, ItemMapper itemMapper, ShoppingListMapper shoppingListMapper) {
        this.digitalStorageService = digitalStorageService;
        this.digitalStorageMapper = digitalStorageMapper;
        this.itemMapper = itemMapper;
        this.shoppingListMapper = shoppingListMapper;
    }

    @PermitAll
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<DigitalStorageDto> findAll(DigitalStorageSearchDto digitalStorageDto, @RequestHeader("Authorization") String jwt) throws AuthenticationException {
        LOGGER.info("findAll({})", digitalStorageDto);

        return digitalStorageMapper.entityListToDtoList(
            digitalStorageService.findAll(digitalStorageDto, jwt)
        );
    }


    @Secured("ROLE_USER")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DigitalStorageDto create(@RequestBody DigitalStorageDto digitalStorageDto, @RequestHeader("Authorization") String jwt) throws ValidationException, ConflictException, AuthenticationException {
        LOGGER.info("create({})", digitalStorageDto);
        return digitalStorageMapper.entityToDto(
            digitalStorageService.create(digitalStorageDto, jwt)
        );
    }

    @PermitAll
    @GetMapping("/items")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemListDto> getStorageItems(ItemSearchDto itemSearchDto, @RequestHeader("Authorization") String jwt) throws ValidationException, AuthenticationException, ConflictException {
        LOGGER.info("getStorageItems({}, {})", itemSearchDto);
        return digitalStorageService.searchItems(itemSearchDto, jwt);
    }

    @PermitAll
    @GetMapping("/info/{name}")
    public List<Item> getItemWithGeneralName(@PathVariable String name, @RequestHeader("Authorization") String jwt) throws AuthenticationException, ValidationException, ConflictException {
        LOGGER.info("getItemWithGeneralName");
        List<Item> items = digitalStorageService.getItemWithGeneralName(name, jwt);
        return digitalStorageService.getItemWithGeneralName(name, jwt);
    }

    @PermitAll
    @PostMapping("/shop")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingItemDto addItemToShopping(@RequestBody ItemDto itemDto, @RequestHeader("Authorization") String jwt) throws AuthenticationException, ValidationException, ConflictException {
        LOGGER.info("addItemToShopping({})", itemDto);
        ShoppingItem item = digitalStorageService.addItemToShopping(itemDto, jwt);
        return itemMapper.entityToShopping(item, shoppingListMapper.entityToDto(item.getShoppingList()));
    }
}
