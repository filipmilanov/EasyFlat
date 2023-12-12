package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.IngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.LabelMapper;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/shopping")
public class ShoppingListEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ShoppingListService shoppingService;
    private final ItemMapper itemMapper;
    private final ShoppingListMapper shoppingListMapper;

    private final IngredientMapper ingredientsMapper;
    private final LabelMapper labelMapper;

    public ShoppingListEndpoint(ShoppingListService shoppingService, ItemMapper mapper, ShoppingListMapper shoppingListMapper, IngredientMapper ingredientsMapper, LabelMapper labelMapper) {
        this.shoppingService = shoppingService;
        this.itemMapper = mapper;
        this.shoppingListMapper = shoppingListMapper;
        this.ingredientsMapper = ingredientsMapper;
        this.labelMapper = labelMapper;
    }

    @Secured("ROLE_USER")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingItemDto create(@RequestBody ShoppingItemDto itemDto) throws ValidationException, ConflictException {
        LOGGER.info("create({})", itemDto);
        ShoppingItem item = shoppingService.create(itemDto);
        return itemMapper.entityToShopping(item, shoppingListMapper.entityToDto(item.getShoppingList()));
    }

    @Secured("ROLE_USER")
    @GetMapping("{itemId}")
    public Optional<ShoppingItemDto> getById(@PathVariable Long itemId) {
        LOGGER.info("findById({})", itemId);
        Optional<ShoppingItem> item = shoppingService.getById(itemId);

        return item.flatMap(currentItem -> Optional.ofNullable(itemMapper.entityToShopping(currentItem,
            shoppingListMapper.entityToDto(currentItem.getShoppingList()))));
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
        List<ShoppingItemDto> ret = new ArrayList<>();
        for (ShoppingItem item : items) {
            ret.add(itemMapper.entityToShopping(item, shoppingListMapper.entityToDto(item.getShoppingList())));
        }
        return ret;
    }

    @PermitAll
    @PostMapping("/list-create")
    public ShoppingListDto createList(@RequestBody String listName) {
        LOGGER.info("createList({})", listName);
        ShoppingList shoppingList = shoppingService.createList(listName);
        return shoppingListMapper.entityToDto(shoppingList);
    }

    @PermitAll
    @DeleteMapping("/{itemId}")
    public ShoppingItemDto deleteItem(@PathVariable Long itemId) {
        LOGGER.info("deleteItem({})", itemId);
        ShoppingItem deletedItem = shoppingService.deleteItem(itemId);
        return itemMapper.entityToShopping(deletedItem, shoppingListMapper.entityToDto(deletedItem.getShoppingList()));
    }

    @PermitAll
    @DeleteMapping("/delete/{shopId}")
    public ShoppingListDto deleteList(@PathVariable Long shopId) {
        LOGGER.info("deleteList({})", shopId);
        ShoppingList deletedList = shoppingService.deleteList(shopId);
        return shoppingListMapper.entityToDto(deletedList);
    }

    @PermitAll
    @GetMapping("/lists")
    public List<ShoppingListDto> getShoppingLists() {
        LOGGER.info("getShoppingLists()");
        List<ShoppingList> lists = shoppingService.getShoppingLists();

        return shoppingListMapper.entityListToDtoList(lists);
    }

    @PermitAll
    @PostMapping("/storage")
    public List<ItemDto> transferToStorage(@RequestBody List<ShoppingItemDto> items) {
        LOGGER.info("transferToStorage({})", items);
        List<Item> res = this.shoppingService.transferToServer(items);
        List<ItemDto> toRet = new ArrayList<>();
        for (Item item : res) {
            toRet.add(itemMapper.entityToDto(item));
        }
        return toRet;
    }


}


