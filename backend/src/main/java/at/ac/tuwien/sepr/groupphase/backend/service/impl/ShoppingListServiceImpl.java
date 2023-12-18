package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.IngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShoppingListMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemLabel;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.DigitalStorageRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemService;
import at.ac.tuwien.sepr.groupphase.backend.service.LabelService;
import at.ac.tuwien.sepr.groupphase.backend.service.ShoppingListService;
import at.ac.tuwien.sepr.groupphase.backend.service.UnitService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.authenticator.Authorization;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.ShoppingItemValidator;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.ShoppingListValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ShoppingListServiceImpl implements ShoppingListService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ShoppingItemRepository shoppingItemRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListMapper shoppingListMapper;
    private final LabelService labelService;
    private final ItemMapper itemMapper;
    private final IngredientMapper ingredientMapper;
    private final ItemRepository itemRepository;
    private final DigitalStorageService digitalStorageService;
    private final ItemService itemService;
    private CustomUserDetailService customUserDetailService;

    private final Authorization authorization;
    private final SharedFlatService sharedFlatService;
    private final DigitalStorageRepository digitalStorageRepository;
    private final ShoppingItemValidator shoppingItemValidator;
    private final UnitService unitService;
    private final ShoppingListValidator validator;

    public ShoppingListServiceImpl(ShoppingItemRepository shoppingItemRepository, ShoppingListRepository shoppingListRepository,
                                   ShoppingListMapper shoppingListMapper, LabelService labelService, ItemMapper itemMapper,
                                   IngredientMapper ingredientMapper, ItemRepository itemRepository, DigitalStorageService digitalStorageService,
                                   ItemService itemService, CustomUserDetailService customUserDetailService, Authorization authorization,
                                   SharedFlatService sharedFlatService, DigitalStorageRepository digitalStorageRepository, ShoppingItemValidator shoppingItemValidator, UnitService unitService, ShoppingListValidator validator) {
        this.shoppingItemRepository = shoppingItemRepository;
        this.labelService = labelService;
        this.itemMapper = itemMapper;
        this.shoppingListRepository = shoppingListRepository;
        this.shoppingListMapper = shoppingListMapper;
        this.ingredientMapper = ingredientMapper;
        this.itemRepository = itemRepository;
        this.digitalStorageService = digitalStorageService;
        this.itemService = itemService;
        this.customUserDetailService = customUserDetailService;
        this.authorization = authorization;
        this.sharedFlatService = sharedFlatService;
        this.digitalStorageRepository = digitalStorageRepository;
        this.shoppingItemValidator = shoppingItemValidator;
        this.unitService = unitService;
        this.validator = validator;
    }

    @Override
    public ShoppingItem create(ShoppingItemDto itemDto, String jwt) throws AuthorizationException, ValidationException, ConflictException {
        LOGGER.trace("create({},{})", itemDto, jwt);
        List<ShoppingList> shoppingLists = this.getShoppingLists(jwt);
        List<DigitalStorage> digitalStorageList = digitalStorageService.findAll(null, jwt);
        List<Unit> unitList = unitService.findAll();
        shoppingItemValidator.validateForCreate(itemDto, shoppingLists, digitalStorageList, unitList);

        ApplicationUser applicationUser = customUserDetailService.getUser(jwt);
        if (applicationUser == null) {
            throw new AuthorizationException("Authentication failed", List.of("User does not exist"));
        }
        List<ItemLabel> labels = findLabelsAndCreateMissing(itemDto.labels());

        ShoppingItem createdItem = shoppingItemRepository.save(itemMapper.dtoToShopping(itemDto, labels, shoppingListMapper.dtoToEntity(itemDto.shoppingList())));
        createdItem.setLabels(labels);
        return createdItem;
    }

    @Override
    public Optional<ShoppingItem> getById(Long itemId, String jwt) throws AuthorizationException {
        LOGGER.trace("getById({},{})", itemId, jwt);
        ApplicationUser applicationUser = customUserDetailService.getUser(jwt);
        if (applicationUser == null) {
            throw new AuthorizationException("Authentication failed", List.of("User does not exist"));
        }
        if (itemId == null) {
            return Optional.empty();
        }
        Optional<ShoppingItem> itemOptional = shoppingItemRepository.findById(itemId);
        if (itemOptional.isPresent()) {
            ShoppingItem item = itemOptional.get();
            if (!item.getShoppingList().getSharedFlat().equals(applicationUser.getSharedFlat())) {
                throw new AuthorizationException("Authentication error", List.of("User has no access to this shopping items"));
            }
        }

        return shoppingItemRepository.findById(itemId);
    }

    @Override
    public Optional<ShoppingList> getShoppingListByName(String name, String jwt) throws AuthorizationException {
        LOGGER.trace("getShoppingListByName({},{})", name, jwt);
        ApplicationUser applicationUser = customUserDetailService.getUser(jwt);
        if (applicationUser == null) {
            throw new AuthorizationException("Authentication failed", List.of("User does not exist"));
        }
        if (name == null) {
            return Optional.empty();
        }
        return shoppingListRepository.getByNameAndSharedFlatIs(name, applicationUser.getSharedFlat());
    }

    @Override
    public Optional<ShoppingList> getShoppingListById(Long id, String jwt) throws AuthorizationException {
        LOGGER.trace("getShoppingListById({},{})", id, jwt);
        ApplicationUser applicationUser = customUserDetailService.getUser(jwt);
        if (applicationUser == null) {
            throw new AuthorizationException("Authentication failed", List.of("User does not exist"));
        }
        if (id == null) {
            return Optional.empty();
        }
        return shoppingListRepository.getByShopListIdAndSharedFlatIs(id, applicationUser.getSharedFlat());
    }

    @Override
    public List<ShoppingItem> getItemsByName(String name, ShoppingItemSearchDto itemSearchDto, String jwt) throws AuthorizationException {
        LOGGER.trace("getItemsById({},{},{})", name, itemSearchDto, jwt);
        ApplicationUser applicationUser = customUserDetailService.getUser(jwt);
        if (applicationUser == null) {
            throw new AuthorizationException("Authentication failed", List.of("User does not exist"));
        }
        List<ShoppingItem> shoppingItems = shoppingItemRepository.searchItems(name,
            (itemSearchDto.productName() != null) ? itemSearchDto.productName() : null,
            (itemSearchDto.label() != null) ? itemSearchDto.label() : null);
        List<ShoppingItem> ret = new ArrayList<>();
        for (ShoppingItem item : shoppingItems) {
            if (item.getShoppingList().getSharedFlat().equals(applicationUser.getSharedFlat())) {
                ret.add(item);
            }
        }
        return ret;
    }

    @Override
    public ShoppingList createList(String listName, String jwt) throws ValidationException, AuthorizationException, ConflictException {
        LOGGER.trace("createList({},{})", listName, jwt);
        ApplicationUser applicationUser = customUserDetailService.getUser(jwt);
        if (applicationUser == null) {
            throw new AuthorizationException("Authentication failed", List.of("User does not exist"));
        }

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setName(listName);
        shoppingList.setSharedFlat(applicationUser.getSharedFlat());
        if (shoppingListRepository.findByNameAndSharedFlatIs(listName, applicationUser.getSharedFlat()) != null) {
            throw new ValidationException("Validation error", List.of("List name already exists"));
        }
        validator.validateForCreate(shoppingList);
        return shoppingListRepository.save(shoppingList);
    }

    @Override
    public ShoppingItem deleteItem(Long itemId, String jwt) throws AuthorizationException {
        LOGGER.trace("deleteItem({},{})", itemId, jwt);
        ApplicationUser applicationUser = customUserDetailService.getUser(jwt);
        if (applicationUser == null) {
            throw new AuthorizationException("Authentication failed", List.of("User does not exist"));
        }
        Optional<ShoppingItem> toDeleteOptional = shoppingItemRepository.findById(itemId);

        if (toDeleteOptional.isPresent()) {
            ShoppingItem toDelete = toDeleteOptional.get();
            if (!toDelete.getShoppingList().getSharedFlat().equals(applicationUser.getSharedFlat())) {
                throw new AuthorizationException("Authentication wrong", List.of("User can not delete this item"));
            }
            shoppingItemRepository.deleteById(itemId);
            return toDelete;
        } else {
            throw new NoSuchElementException("Item with this id does not exist!");
        }
    }

    @Override
    public ShoppingList deleteList(Long shopId, String jwt) throws ValidationException, AuthorizationException {
        LOGGER.trace("deleteList({},{})", shopId, jwt);
        ApplicationUser applicationUser = customUserDetailService.getUser(jwt);
        if (applicationUser == null) {
            throw new AuthorizationException("Authentication failed", List.of("User does not exist"));
        }
        Optional<ShoppingList> toDeleteOptional = shoppingListRepository.findById(shopId);
        if (toDeleteOptional.isPresent()) {
            ShoppingList toDelete = toDeleteOptional.get();

            if (toDelete.getName().equals("Default")) {
                throw new ValidationException("Default list can not be deleted!", null);
            }
            List<ShoppingItem> items = shoppingItemRepository.findByShoppingListId(shopId);
            shoppingItemRepository.deleteAll(items);
            shoppingListRepository.deleteById(shopId);
            return toDelete;
        } else {
            throw new NoSuchElementException("Shopping list with this id does not exist!");
        }
    }

    @Override
    public List<ShoppingList> getShoppingLists(String jwt) throws AuthorizationException {
        LOGGER.trace("getShoppingLists({})", jwt);
        ApplicationUser applicationUser = customUserDetailService.getUser(jwt);
        if (applicationUser == null) {
            throw new AuthorizationException("Authentication failed", List.of("User does not exist"));
        }
        return shoppingListRepository.findBySharedFlatIs(applicationUser.getSharedFlat());
    }

    @Override
    public List<Item> transferToServer(List<ShoppingItemDto> items, String jwt) throws AuthorizationException {
        LOGGER.trace("transferToServer({},{})", items, jwt);
        ApplicationUser applicationUser = customUserDetailService.getUser(jwt);
        if (applicationUser == null) {
            throw new AuthorizationException("Authentication failed", List.of("User does not exist"));
        }
        List<DigitalStorage> storage = digitalStorageRepository.findByTitleContainingAndSharedFlatIs("Storage", applicationUser.getSharedFlat());
        List<Item> itemsList = new ArrayList<>();
        for (ShoppingItemDto itemDto : items) {
            Item item;
            if (itemDto.alwaysInStock() != null && itemDto.alwaysInStock()) {
                item = shoppingListMapper.shoppingItemDtoToAis(itemDto, ingredientMapper.dtoListToEntityList(itemDto.ingredients()), storage.get(0));
            } else {
                item = shoppingListMapper.shoppingItemDtoToItem(itemDto, ingredientMapper.dtoListToEntityList(itemDto.ingredients()), storage.get(0));
            }
            itemRepository.save(item);
            shoppingItemRepository.deleteById(itemDto.itemId());
            itemsList.add(item);
        }
        return itemsList;
    }

    @Override
    public ShoppingItem update(ShoppingItemDto itemDto, String jwt) throws ConflictException, AuthorizationException, ValidationException {
        LOGGER.trace("update({})", itemDto);

        List<ShoppingList> shoppingLists = this.getShoppingLists(jwt);
        List<DigitalStorage> digitalStorageList = digitalStorageService.findAll(null, jwt);
        List<Unit> unitList = unitService.findAll();
        shoppingItemValidator.validateForUpdate(itemDto, shoppingLists, digitalStorageList, unitList);

        List<ItemLabel> labels = null;
        if (itemDto.labels() != null) {
            labels = findLabelsAndCreateMissing(itemDto.labels());
        }
        List<Ingredient> ingredientList = itemService.findIngredientsAndCreateMissing(itemDto.ingredients());

        ShoppingItem item = itemMapper.dtoToShopping(itemDto, labels,
            shoppingListMapper.dtoToEntity(itemDto.shoppingList()));

        ShoppingItem updatedItem = shoppingItemRepository.save(item);
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
