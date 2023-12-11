package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShoppingListMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.ShoppingListService;
import jakarta.annotation.security.PermitAll;
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
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/shopping")
public class ShoppingListEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ShoppingListService shoppingService;
    private final ItemMapper itemMapper;
    private final ShoppingListMapper shoppingListMapper;

    public ShoppingListEndpoint(ShoppingListService shoppingService, ItemMapper mapper, ShoppingListMapper shoppingListMapper) {
        this.shoppingService = shoppingService;
        this.itemMapper = mapper;
        this.shoppingListMapper = shoppingListMapper;
    }

    @Secured("ROLE_USER")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingItemDto create(@RequestBody ShoppingItemDto itemDto) throws ValidationException, ConflictException {
        LOGGER.info("create({})", itemDto);
        return itemMapper.entityToShopping(shoppingService.create(itemDto));
    }

    @Secured("ROLE_USER")
    @GetMapping("{itemId}")
    public Optional<ShoppingItemDto> getById(@PathVariable Long itemId) {
        LOGGER.info("findById({})", itemId);
        Optional<ShoppingItem> item = shoppingService.getById(itemId);

        return item.flatMap(currentItem -> Optional.ofNullable(itemMapper.entityToShopping(currentItem)));
    }


    @Secured("ROLE_USER")
    @GetMapping("/list/{id}")
    public Optional<ShoppingListDto> getShoppingListById(@PathVariable Long id) {
        Optional<ShoppingList> ret = shoppingService.getShoppingListById(id);

        return ret.flatMap(shoppingList -> Optional.ofNullable(shoppingListMapper.entityToDto(shoppingList)));

    }

    @PermitAll
    @GetMapping("/list-items/{listId}")
    public List<ShoppingItemDto> getItemsById(@PathVariable Long listId) {
        LOGGER.info("getItemsById({})", listId);
        List<ShoppingItem> items = shoppingService.getItemsById(listId);

        return itemMapper.shoppingItemListToShoppingDto(items);
    }

    @PermitAll
    @PostMapping("/list-create")
    public ShoppingListDto createList(@RequestBody String listName) {
        LOGGER.info("createList({})", listName);
        ShoppingList shoppingList = shoppingService.createList(listName);
        return shoppingListMapper.entityToDto(shoppingList);
    }
}


