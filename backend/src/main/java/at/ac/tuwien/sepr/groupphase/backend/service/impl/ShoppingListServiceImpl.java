package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.IngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.LabelMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShoppingListMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemLabel;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemService;
import at.ac.tuwien.sepr.groupphase.backend.service.LabelService;
import at.ac.tuwien.sepr.groupphase.backend.service.ShoppingListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ShoppingListServiceImpl implements ShoppingListService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ShoppingRepository shoppingRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListMapper shoppingListMapper;
    private final LabelService labelService;
    private final ItemMapper itemMapper;
    private final IngredientMapper ingredientMapper;
    private final ItemRepository itemRepository;
    private final DigitalStorageService digitalStorageService;
    private final ItemService itemService;
    private CustomUserDetailService customUserDetailService;

    public ShoppingListServiceImpl(ShoppingRepository shoppingRepository, ShoppingListRepository shoppingListRepository,
                                   ShoppingListMapper shoppingListMapper, LabelService labelService, ItemMapper itemMapper,
                                   IngredientMapper ingredientMapper, ItemRepository itemRepository, DigitalStorageService digitalStorageService,
                                   ItemService itemService, LabelMapper labelMapper, CustomUserDetailService customUserDetailService) {
        this.shoppingRepository = shoppingRepository;
        this.labelService = labelService;
        this.itemMapper = itemMapper;
        this.shoppingListRepository = shoppingListRepository;
        this.shoppingListMapper = shoppingListMapper;
        this.ingredientMapper = ingredientMapper;
        this.itemRepository = itemRepository;
        this.digitalStorageService = digitalStorageService;
        this.itemService = itemService;
        this.customUserDetailService = customUserDetailService;
    }

    @Override
    public ShoppingItem create(ShoppingItemDto itemDto) {
        LOGGER.trace("create({})", itemDto);
        List<ItemLabel> labels = findLabelsAndCreateMissing(itemDto.labels());

        ShoppingItem createdItem = shoppingRepository.save(itemMapper.dtoToShopping(itemDto, labels, shoppingListMapper.dtoToEntity(itemDto.shoppingList())));
        createdItem.setLabels(labels);
        return createdItem;
    }

    @Override
    public Optional<ShoppingItem> getById(Long itemId) {
        LOGGER.trace("getById({})", itemId);
        if (itemId == null) {
            return Optional.empty();
        }

        return shoppingRepository.findById(itemId);
    }

    @Override
    public Optional<ShoppingList> getShoppingListById(Long shopListId) {
        LOGGER.trace("getShoppingListById({})", shopListId);
        if (shopListId == null) {
            return Optional.empty();
        }

        return shoppingListRepository.getByShopListId(shopListId);
    }

    @Override
    public List<ShoppingItem> getItemsById(Long listId, ShoppingItemSearchDto itemSearchDto) {
        LOGGER.trace("getItemsById({}, {})", listId, itemSearchDto);
        List<ShoppingItem> shoppingItems = shoppingRepository.searchItems(listId,
            (itemSearchDto.productName() != null) ? itemSearchDto.productName() : null,
            (itemSearchDto.label() != null) ? itemSearchDto.label() : null);
        return shoppingRepository.saveAll(shoppingItems);
    }

    @Override
    public ShoppingList createList(String listName) {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setName(listName);
        return shoppingListRepository.save(shoppingList);
    }

    @Override
    public ShoppingItem deleteItem(Long itemId) {
        Optional<ShoppingItem> toDeleteOptional = shoppingRepository.findById(itemId);

        if (toDeleteOptional.isPresent()) {
            ShoppingItem toDelete = toDeleteOptional.get();
            shoppingRepository.deleteById(itemId);
            return toDelete;
        } else {
            throw new NoSuchElementException("Item with this id does not exist!");
        }
    }

    @Override
    public ShoppingList deleteList(Long shopId) {
        if (shopId == 1) {
            throw new BadCredentialsException("The main list can not be deleted!");
        }
        Optional<ShoppingList> toDeleteOptional = shoppingListRepository.findById(shopId);

        if (toDeleteOptional.isPresent()) {
            ShoppingList toDelete = toDeleteOptional.get();
            List<ShoppingItem> items = shoppingRepository.findByShoppingListId(shopId);
            shoppingRepository.deleteAll(items);
            shoppingListRepository.deleteById(shopId);
            return toDelete;
        } else {
            throw new NoSuchElementException("Shopping list with this id does not exist!");
        }
    }

    @Override
    public List<ShoppingList> getShoppingLists(String jwt) throws AuthenticationException {
        LOGGER.trace("getShoppingLists({})", jwt);
        ApplicationUser applicationUser = customUserDetailService.getUser(jwt);
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exist"));
        }
        return shoppingListRepository.findBySharedFlatIs(applicationUser.getSharedFlat());
    }

    @Override
    public List<Item> transferToServer(List<ShoppingItemDto> items) {
        LOGGER.trace("transferToServer({})", items);
        List<Item> itemsList = new ArrayList<>();
        for (ShoppingItemDto itemDto : items) {
            Item item;
            if (itemDto.alwaysInStock() != null && itemDto.alwaysInStock()) {
                item = shoppingListMapper.shoppingItemDtoToAis(itemDto, ingredientMapper.dtoListToEntityList(itemDto.ingredients()), new DigitalStorage());
            } else {
                item = shoppingListMapper.shoppingItemDtoToItem(itemDto, ingredientMapper.dtoListToEntityList(itemDto.ingredients()), new DigitalStorage());
            }
            itemRepository.save(item);
            shoppingRepository.deleteById(itemDto.itemId());
            itemsList.add(item);
        }
        return itemsList;
    }

    @Override
    public ShoppingItem update(ShoppingItemDto itemDto) throws ConflictException {
        LOGGER.trace("update({})", itemDto);
        List<ItemLabel> labels = null;
        if (itemDto.labels() != null) {
            labels = findLabelsAndCreateMissing(itemDto.labels());
        }
        List<Ingredient> ingredientList = itemService.findIngredientsAndCreateMissing(itemDto.ingredients());

        ShoppingItem item = itemMapper.dtoToShopping(itemDto, labels,
            shoppingListMapper.dtoToEntity(itemDto.shoppingList()));

        ShoppingItem updatedItem = shoppingRepository.save(item);
        updatedItem.setIngredientList(ingredientList);
        updatedItem.setLabels(labels);
        return updatedItem;
    }


    private List<ItemLabel> findLabelsAndCreateMissing(List<ItemLabelDto> labels) {
        LOGGER.trace("findLabelsAndCreateMissing({})", labels);
        if (labels == null) {
            return List.of();
        }
        List<String> values = labels.stream()
            .map(ItemLabelDto::labelValue)
            .toList();
        List<String> colours = labels.stream()
            .map(ItemLabelDto::labelColour)
            .toList();

        List<ItemLabel> ret = new ArrayList<>();
        if (!values.isEmpty()) {
            for (int i = 0; i < values.size(); i++) {
                ItemLabel found = labelService.findByValueAndColour(values.get(i), colours.get(i));
                if (found != null) {
                    ret.add(found);
                }
            }
        }

        List<ItemLabelDto> missingLabels = labels.stream()
            .filter(labelDto ->
                ret.stream()
                    .noneMatch(label ->
                        (label.getLabelValue().equals(labelDto.labelValue())
                            && label.getLabelColour().equals(labelDto.labelColour()))
                    )
            ).toList();

        if (!missingLabels.isEmpty()) {
            List<ItemLabel> createdLabels = labelService.createAll(missingLabels);
            ret.addAll(createdLabels);
        }
        return ret;
    }
}
