package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.IngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.LabelMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShoppingListMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

    public ShoppingListEndpoint(ShoppingListService shoppingService, ItemMapper mapper, ShoppingListMapper shoppingListMapper, IngredientMapper ingredientsMapper, LabelMapper labelMapper) {
        this.shoppingService = shoppingService;
        this.itemMapper = mapper;
        this.shoppingListMapper = shoppingListMapper;
    }

    @Secured("ROLE_USER")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingItemDto create(@RequestBody ShoppingItemDto itemDto, @RequestHeader("Authorization") String jwt) throws ValidationException, ConflictException, AuthenticationException {
        LOGGER.info("create({})", itemDto);
        ShoppingItem item = shoppingService.create(itemDto, jwt);
        return itemMapper.entityToShopping(item, shoppingListMapper.entityToDto(item.getShoppingList()));
    }

    @Secured("ROLE_USER")
    @PutMapping("{id}")
    public ShoppingItemDto update(@PathVariable long id, @RequestBody ShoppingItemDto itemDto,
                                  @RequestHeader("Authorization") String jwt) throws ValidationException, ConflictException {
        LOGGER.info("update({},{},{})", id, itemDto, jwt);
        ShoppingItem item = shoppingService.update(itemDto.withId(id), jwt);
        return itemMapper.entityToShopping(item, shoppingListMapper.entityToDto(item.getShoppingList()));
    }

    @Secured("ROLE_USER")
    @GetMapping("{itemId}")
    public Optional<ShoppingItemDto> getById(@PathVariable Long itemId, @RequestHeader("Authorization") String jwt) throws AuthenticationException {
        LOGGER.info("getById({},{})", itemId, jwt);
        Optional<ShoppingItem> item = shoppingService.getById(itemId, jwt);
        return item.flatMap(currentItem -> Optional.ofNullable(itemMapper.entityToShopping(currentItem,
            shoppingListMapper.entityToDto(currentItem.getShoppingList()))));
    }


    @Secured("ROLE_USER")
    @GetMapping("/list/{name}")
    public Optional<ShoppingListDto> getShoppingListByName(@PathVariable String name, @RequestHeader("Authorization") String jwt) throws AuthenticationException {
        LOGGER.info("getShoppingListById({},{})", name, jwt);
        Optional<ShoppingList> ret = shoppingService.getShoppingListByName(name, jwt);
        return ret.flatMap(shoppingList -> Optional.ofNullable(shoppingListMapper.entityToDto(shoppingList)));
    }

    @Secured("ROLE_USER")
    @GetMapping("/listId/{id}")
    public Optional<ShoppingListDto> getShoppingListById(@PathVariable Long id, @RequestHeader("Authorization") String jwt) throws AuthenticationException {
        LOGGER.info("getShoppingListById({},{})", id, jwt);
        Optional<ShoppingList> ret = shoppingService.getShoppingListById(id, jwt);
        return ret.flatMap(shoppingList -> Optional.ofNullable(shoppingListMapper.entityToDto(shoppingList)));

    }

    @PermitAll
    @GetMapping("/list-items/{name}")
    public List<ShoppingItemDto> getItemsByName(@PathVariable String name, ShoppingItemSearchDto itemSearchDto,
                                              @RequestHeader("Authorization") String jwt) throws AuthenticationException {
        LOGGER.info("getItemsById({},{})", name, jwt);
        List<ShoppingItem> items = shoppingService.getItemsByName(name, itemSearchDto, jwt);
        List<ShoppingItemDto> ret = new ArrayList<>();
        for (ShoppingItem item : items) {
            ret.add(itemMapper.entityToShopping(item, shoppingListMapper.entityToDto(item.getShoppingList())));
        }
        return ret;
    }

    @PermitAll
    @PostMapping("/list-create")
    public ShoppingListDto createList(@RequestBody String listName, @RequestHeader("Authorization") String jwt) throws ValidationException, AuthenticationException {
        LOGGER.info("createList({},{})", listName, jwt);
        ShoppingList shoppingList = shoppingService.createList(listName, jwt);
        return shoppingListMapper.entityToDto(shoppingList);
    }

    @PermitAll
    @DeleteMapping("/{itemId}")
    public ShoppingItemDto deleteItem(@PathVariable Long itemId, @RequestHeader("Authorization") String jwt) throws AuthenticationException {
        LOGGER.info("deleteItem({},{})", itemId, jwt);
        ShoppingItem deletedItem = shoppingService.deleteItem(itemId, jwt);
        return itemMapper.entityToShopping(deletedItem, shoppingListMapper.entityToDto(deletedItem.getShoppingList()));
    }

    @PermitAll
    @DeleteMapping("/delete/{shopId}")
    public ShoppingListDto deleteList(@PathVariable Long shopId, @RequestHeader("Authorization") String jwt) throws ValidationException, AuthenticationException {
        LOGGER.info("deleteList({},{})", shopId, jwt);
        ShoppingList deletedList = shoppingService.deleteList(shopId, jwt);
        return shoppingListMapper.entityToDto(deletedList);
    }

    @PermitAll
    @GetMapping("/lists")
    public List<ShoppingListDto> getShoppingLists(@RequestHeader("Authorization") String jwt) throws AuthenticationException {
        LOGGER.info("getShoppingLists({})", jwt);
        List<ShoppingList> lists = shoppingService.getShoppingLists(jwt);

        return shoppingListMapper.entityListToDtoList(lists);
    }

    @PermitAll
    @PostMapping("/storage")
    public List<ItemDto> transferToStorage(@RequestBody List<ShoppingItemDto> items, @RequestHeader("Authorization") String jwt) throws AuthenticationException {
        LOGGER.info("transferToStorage({},{})", items, jwt);
        List<Item> res = this.shoppingService.transferToServer(items, jwt);
        List<ItemDto> toRet = new ArrayList<>();
        for (Item item : res) {
            toRet.add(itemMapper.entityToDto(item));
        }
        return toRet;
    }



}
