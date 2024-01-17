package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.IngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShoppingListMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemLabel;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.DigitalStorageRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import at.ac.tuwien.sepr.groupphase.backend.service.IngredientService;
import at.ac.tuwien.sepr.groupphase.backend.service.LabelService;
import at.ac.tuwien.sepr.groupphase.backend.service.ShoppingListService;
import at.ac.tuwien.sepr.groupphase.backend.service.UnitService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.ShoppingItemValidator;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.ShoppingListValidatorImpl;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
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
    private final IngredientService ingredientService;
    private CustomUserDetailService customUserDetailService;
    private final DigitalStorageRepository digitalStorageRepository;
    private final ShoppingItemValidator shoppingItemValidator;
    private final UnitService unitService;
    private final ShoppingListValidatorImpl validator;
    private final AuthService authService;

    public ShoppingListServiceImpl(ShoppingItemRepository shoppingItemRepository, ShoppingListRepository shoppingListRepository,
                                   ShoppingListMapper shoppingListMapper, LabelService labelService, ItemMapper itemMapper,
                                   IngredientMapper ingredientMapper, ItemRepository itemRepository, DigitalStorageService digitalStorageService,
                                   IngredientService ingredientService, CustomUserDetailService customUserDetailService, DigitalStorageRepository digitalStorageRepository,
                                   ShoppingItemValidator shoppingItemValidator, UnitService unitService, ShoppingListValidatorImpl validator, AuthService authService) {
        this.shoppingItemRepository = shoppingItemRepository;
        this.labelService = labelService;
        this.itemMapper = itemMapper;
        this.shoppingListRepository = shoppingListRepository;
        this.shoppingListMapper = shoppingListMapper;
        this.ingredientMapper = ingredientMapper;
        this.itemRepository = itemRepository;
        this.digitalStorageService = digitalStorageService;
        this.ingredientService = ingredientService;
        this.customUserDetailService = customUserDetailService;
        this.digitalStorageRepository = digitalStorageRepository;
        this.shoppingItemValidator = shoppingItemValidator;
        this.unitService = unitService;
        this.validator = validator;
        this.authService = authService;
    }

    @Override
    @Transactional
    public ShoppingItem create(ShoppingItemDto itemDto, String jwt)
        throws ValidationException, ConflictException, AuthenticationException, AuthorizationException {
        LOGGER.trace("create({},{})", itemDto, jwt);
        List<ShoppingList> shoppingLists = this.getShoppingLists("", jwt);
        List<DigitalStorage> digitalStorageList = digitalStorageService.findAll(null);
        List<Unit> unitList = unitService.findAll();
        shoppingItemValidator.validateForCreate(itemDto, shoppingLists, digitalStorageList, unitList);

        List<ItemLabel> labels = findLabelsAndCreateMissing(itemDto.labels());

        ShoppingItem si = itemMapper.dtoToShopping(itemDto, labels);

        return shoppingItemRepository.save(si);
    }

    @Override
    public Optional<ShoppingItem> getById(Long itemId) throws AuthenticationException {
        LOGGER.trace("getById({})", itemId);
        ApplicationUser applicationUser = authService.getUserFromToken();

        if (itemId == null) {
            return Optional.empty();
        }
        Optional<ShoppingItem> itemOptional = shoppingItemRepository.findById(itemId);
        if (itemOptional.isPresent()) {
            ShoppingItem item = itemOptional.get();
            if (!item.getShoppingList().getSharedFlat().equals(applicationUser.getSharedFlat())) {
                throw new AuthenticationException("Authentication error", List.of("User has no access to this shopping items"));
            }
        }

        return shoppingItemRepository.findById(itemId);
    }

    @Override
    public Optional<ShoppingList> getShoppingListByName(String name, String jwt) throws AuthenticationException {
        LOGGER.trace("getShoppingListByName({},{})", name, jwt);
        ApplicationUser applicationUser = customUserDetailService.getUser(jwt);
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exist"));
        }
        if (name == null) {
            return Optional.empty();
        }
        return shoppingListRepository.getByNameAndSharedFlatIs(name, applicationUser.getSharedFlat());
    }

    @Override
    public Optional<ShoppingList> getShoppingListById(Long id, String jwt) throws AuthenticationException {
        LOGGER.trace("getShoppingListById({},{})", id, jwt);
        ApplicationUser applicationUser = customUserDetailService.getUser(jwt);
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exist"));
        }
        if (id == null) {
            return Optional.empty();
        }
        return shoppingListRepository.getByIdAndSharedFlatIs(id, applicationUser.getSharedFlat());
    }

    @Override
    public List<ShoppingItem> getItemsById(Long id, ShoppingItemSearchDto itemSearchDto, String jwt) throws AuthenticationException {
        LOGGER.trace("getItemsById({},{},{})", id, itemSearchDto, jwt);
        ApplicationUser applicationUser = customUserDetailService.getUser(jwt);
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exist"));
        }
        List<ShoppingItem> shoppingItems = shoppingItemRepository.searchItems(id,
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
    @Transactional
    public ShoppingList createList(String listName) throws ValidationException, AuthenticationException, ConflictException {
        LOGGER.trace("createList({})", listName);
        ApplicationUser applicationUser = authService.getUserFromToken();
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exist"));
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
    @Transactional
    public ShoppingItem deleteItem(Long itemId) throws AuthenticationException {
        LOGGER.trace("deleteItem({})", itemId);
        ApplicationUser applicationUser = authService.getUserFromToken();
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exist"));
        }
        // Input validation
        if (itemId == null || itemId <= 0) {
            throw new IllegalArgumentException("Invalid itemId");
        }
        Optional<ShoppingItem> toDeleteOptional = shoppingItemRepository.findById(itemId);
        if (toDeleteOptional.isPresent()) {
            ShoppingItem toDelete = toDeleteOptional.get();
            // Enhanced authorization
            if (!toDelete.getShoppingList().getSharedFlat().equals(applicationUser.getSharedFlat())) {
                throw new AuthenticationException("Authentication wrong", List.of("User can not delete this item"));
            }
            //toDelete.setLabels(null);
            //shoppingItemRepository.save(toDelete);
            //shoppingItemRepository.deleteById(itemId);
            shoppingItemRepository.delete(toDelete);
            return toDelete;
        } else {
            throw new NoSuchElementException("Item with this id does not exist!");
        }
    }

    @Override
    @Transactional
    public ShoppingList deleteList(Long shopId) throws ValidationException, AuthenticationException, AuthorizationException {
        LOGGER.trace("deleteList({})", shopId);

        // Authentication (check the correct user)
        ApplicationUser applicationUser = authService.getUserFromToken();

        // Authorization (check if the user can work with this object)
        ShoppingList check = shoppingListRepository.findByIdAndSharedFlatIs(shopId, applicationUser.getSharedFlat());
        if (check == null) {
            throw new AuthorizationException("Authorization failed", List.of("User has no access to this shopping list!"));
        }

        // Attempt to find and delete the shopping list
        Optional<ShoppingList> deletedListOptional = shoppingListRepository.findById(shopId);
        if (deletedListOptional.isPresent()) {
            ShoppingList deletedList = deletedListOptional.get();
            for (int i = 0; i < deletedList.getItems().size(); i++) {
                this.deleteItem(deletedList.getItems().get(i).getItemId());
            }
            shoppingListRepository.deleteById(shopId);
            return deletedList;
        } else {
            throw new ValidationException("Validation failed", List.of("Shopping list not found"));
        }
    }


    @Override
    public List<ShoppingList> getShoppingLists(String name, String jwt) throws AuthenticationException {
        LOGGER.trace("getShoppingLists({})", jwt);
        ApplicationUser applicationUser = customUserDetailService.getUser(jwt);
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exist"));
        }
        List<ShoppingList> ret = shoppingListRepository.findAllByNameContainingIgnoreCaseAndSharedFlatIs(name != null ? name : "", applicationUser.getSharedFlat());
        for (ShoppingList shoppingList : ret) {
            shoppingList.setItems(this.getItemsById(shoppingList.getId(), new ShoppingItemSearchDto(null, null, null), jwt));
        }
        return ret;
    }

    @Override
    @Secured("ROLE_USER")
    public List<DigitalStorageItem> transferToServer(List<ShoppingItemDto> items) throws AuthenticationException {
        LOGGER.trace("transferToServer({})", items);
        ApplicationUser applicationUser = authService.getUserFromToken();
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exist"));
        }
        List<DigitalStorage> storage = digitalStorageRepository.findByTitleContainingAndSharedFlatIs("Storage " + applicationUser.getSharedFlat().getName(), applicationUser.getSharedFlat());
        List<DigitalStorageItem> itemsList = new ArrayList<>();
        for (ShoppingItemDto itemDto : items) {
            DigitalStorageItem item;
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
    @Transactional
    public ShoppingItem update(ShoppingItemDto itemDto, String jwt)
        throws ConflictException, AuthenticationException, ValidationException, AuthorizationException {
        LOGGER.trace("update({})", itemDto);

        List<ShoppingList> shoppingLists = this.getShoppingLists("", jwt);
        List<DigitalStorage> digitalStorageList = digitalStorageService.findAll(null);
        List<Unit> unitList = unitService.findAll();
        shoppingItemValidator.validateForUpdate(itemDto, shoppingLists, digitalStorageList, unitList);

        List<ItemLabel> labels = null;
        if (itemDto.labels() != null) {
            labels = findLabelsAndCreateMissing(itemDto.labels());
        }
        List<Ingredient> ingredientList = ingredientService.findIngredientsAndCreateMissing(itemDto.ingredients());

        ShoppingItem item = itemMapper.dtoToShopping(itemDto, labels);
        item.getItemCache().setIngredientList(ingredientList);
        item.setLabels(labels);
        return shoppingItemRepository.save(item);
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
