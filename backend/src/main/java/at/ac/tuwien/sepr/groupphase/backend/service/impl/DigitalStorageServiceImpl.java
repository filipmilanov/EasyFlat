package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.DigitalStorageMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.IngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlwaysInStockDigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemOrderType;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.DigitalStorageRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import at.ac.tuwien.sepr.groupphase.backend.service.UnitService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.authenticator.Authorization;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.DigitalStorageValidator;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.ItemValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service

public class DigitalStorageServiceImpl implements DigitalStorageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DigitalStorageRepository digitalStorageRepository;
    private final ItemRepository itemRepository;
    private final DigitalStorageMapper digitalStorageMapper;
    private final DigitalStorageValidator digitalStorageValidator;
    private final ShoppingItemRepository shoppingItemRepository;
    private final IngredientMapper ingredientMapper;
    private final Authorization authorization;
    private final AuthService authService;
    private final UnitService unitService;
    private final SharedFlatService sharedFlatService;
    private final ShoppingListRepository shoppingListRepository;
    private final ItemValidator itemValidator;

    public DigitalStorageServiceImpl(DigitalStorageRepository digitalStorageRepository,
                                     ItemRepository itemRepository,
                                     DigitalStorageMapper digitalStorageMapper,
                                     DigitalStorageValidator digitalStorageValidator,
                                     SharedFlatService sharedFlatService,
                                     ShoppingItemRepository shoppingItemRepository,
                                     IngredientMapper ingredientMapper,
                                     AuthService authService,
                                     Authorization authorization,
                                     ShoppingListRepository shoppingListRepository,
                                     UnitService unitService,
                                     ItemValidator itemValidator) {
        this.digitalStorageRepository = digitalStorageRepository;
        this.itemRepository = itemRepository;
        this.digitalStorageMapper = digitalStorageMapper;
        this.digitalStorageValidator = digitalStorageValidator;
        this.shoppingItemRepository = shoppingItemRepository;
        this.ingredientMapper = ingredientMapper;
        this.authService = authService;
        this.authorization = authorization;
        this.sharedFlatService = sharedFlatService;
        this.shoppingListRepository = shoppingListRepository;
        this.unitService = unitService;
        this.itemValidator = itemValidator;
    }

    @Override
    public Optional<DigitalStorage> findById(Long id) {
        LOGGER.trace("findById({})", id);
        if (id == null) {
            return Optional.empty();
        }

        return digitalStorageRepository.findById(id);
    }

    @Override
    public List<DigitalStorage> findAll(DigitalStorageSearchDto digitalStorageSearchDto) throws AuthenticationException {
        LOGGER.trace("findAll({})", digitalStorageSearchDto);

        ApplicationUser applicationUser = authService.getUserFromToken();
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exists"));
        }

        return digitalStorageRepository.findByTitleContainingAndSharedFlatIs(
            (digitalStorageSearchDto != null && digitalStorageSearchDto.title() != null)
                ? digitalStorageSearchDto.title()
                : "",
            applicationUser.getSharedFlat()
        );
    }

    @Override
    public List<DigitalStorageItem> findAllItemsOfStorage(Long id) {
        Optional<DigitalStorage> optionalStorage = digitalStorageRepository.findById(id);
        if (optionalStorage.isPresent()) {
            return optionalStorage.get().getItemList();
        } else {
            return Collections.emptyList();
        }

    }

    @Override
    public List<ItemListDto> searchItems(ItemSearchDto searchItem, String jwt) throws ValidationException, AuthenticationException, ConflictException {
        LOGGER.trace("searchItems({})", searchItem);

        digitalStorageValidator.validateForSearchItems(searchItem);

        Long storageId = getStorageIdForUser(jwt);

        Class alwaysInStock;
        if (searchItem.alwaysInStock() == null || !searchItem.alwaysInStock()) {
            alwaysInStock = DigitalStorageItem.class;
        } else {
            alwaysInStock = AlwaysInStockDigitalStorageItem.class;
        }

        List<DigitalStorageItem> allDigitalStorageItems = digitalStorageRepository.searchItems(
            storageId,
            (searchItem.productName() != null) ? searchItem.productName() : null,
            (searchItem.fillLevel() != null) ? searchItem.fillLevel() : null,
            alwaysInStock
        );

        List<ItemListDto> groupedItems = prepareListItemsForStorage(allDigitalStorageItems);
        return groupedItems.stream().sorted((g1, g2) -> {
            if (searchItem.orderType() == null) {
                return 0;
            }
            if (searchItem.orderType() == ItemOrderType.QUANTITY_CURRENT) {
                if (g1.quantityCurrent() == null) {
                    return -1;
                }
                if (g2.quantityCurrent() == null) {
                    return 1;
                }
                return g1.quantityCurrent().compareTo(g2.quantityCurrent());
            } else if (searchItem.orderType() == ItemOrderType.PRODUCT_NAME) {
                if (g1.generalName() == null) {
                    return 1;
                }
                if (g2.generalName() == null) {
                    return -1;
                }
                return g1.generalName().compareTo(g2.generalName());
            } else {
                return 0;
            }
        }).toList();
    }

    @Transactional
    @Override
    public DigitalStorage create(DigitalStorageDto storageDto, String jwt) throws ConflictException, ValidationException, AuthenticationException {
        LOGGER.trace("create({})", storageDto);


        digitalStorageValidator.validateForCreate(storageDto);

        List<Long> allowedUser = sharedFlatService.findById(
                storageDto.sharedFlat().getId(),
                jwt
            ).getUsers().stream()
            .map(ApplicationUser::getId)
            .toList();
        authorization.authenticateUser(
            allowedUser,
            "The given digital storage does not belong to the user's shared flat!"
        );


        DigitalStorage storage = digitalStorageMapper.dtoToEntity(storageDto);

        return digitalStorageRepository.save(storage);
    }

    @Transactional
    @Override
    public ShoppingItem addItemToShopping(ItemDto itemDto, String jwt) throws AuthenticationException {
        LOGGER.trace("addItemToShopping({})", itemDto);

        ApplicationUser applicationUser = authService.getUserFromToken();
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exist"));
        }
        ShoppingList shoppingList = shoppingListRepository.findByNameAndSharedFlatIs("Default", applicationUser.getSharedFlat());
        ShoppingItem shoppingItem = itemMapper.itemDtoToShoppingItem(itemDto,
            ingredientMapper.dtoListToEntityList(itemDto.ingredients()),
            shoppingList
        );
        return shoppingItemRepository.save(shoppingItem);
    }

    private List<ItemListDto> prepareListItemsForStorage(List<DigitalStorageItem> allDigitalStorageItems) throws ValidationException, ConflictException {
        Map<String, Double[]> items = new HashMap<>();
        Map<String, Unit> itemUnits = new HashMap<>();
        Unit unit = null;
        for (DigitalStorageItem digitalStorageItem : allDigitalStorageItems) {
            itemUnits.computeIfAbsent(digitalStorageItem.getItemCache().getGeneralName(), k -> digitalStorageItem.getItemCache().getUnit());

            double currentQ = 0;
            double totalQ = 0;
            if (items.get(digitalStorageItem.getItemCache().getGeneralName()) != null) {
                currentQ = items.get(digitalStorageItem.getItemCache().getGeneralName())[0];
                totalQ = items.get(digitalStorageItem.getItemCache().getGeneralName())[2];
            }

            Double updatedQuantityCurrent = unitService.convertUnits(digitalStorageItem.getItemCache().getUnit(), itemUnits.get(digitalStorageItem.getItemCache().getGeneralName()), digitalStorageItem.getQuantityCurrent());
            Double updatedQuantityTotal = unitService.convertUnits(digitalStorageItem.getItemCache().getUnit(), itemUnits.get(digitalStorageItem.getItemCache().getGeneralName()), digitalStorageItem.getItemCache().getQuantityTotal());


            Double[] quantityStorageId = new Double[3];
            quantityStorageId[0] = currentQ + updatedQuantityCurrent;
            quantityStorageId[1] = digitalStorageItem.getDigitalStorage().getStorageId().doubleValue();
            quantityStorageId[2] = totalQ + updatedQuantityTotal;
            items.put(digitalStorageItem.getItemCache().getGeneralName(), quantityStorageId);
        }
        List<ItemListDto> toRet = new LinkedList<>();
        for (Map.Entry<String, Double[]> item : items.entrySet()) {
            toRet.add(new ItemListDto(item.getKey(), item.getValue()[0], item.getValue()[2], item.getValue()[1].longValue(), UnitDtoBuilder.builder().name(itemUnits.get(item.getKey()).getName()).build()));
        }
        return toRet;
    }

    /**
     * The Method assume, that there is only one storage per sharedFlat.
     */
    private Long getStorageIdForUser(String jwt) throws AuthenticationException, ValidationException, ConflictException {
        List<DigitalStorage> digitalStorageList = findAll(null);
        DigitalStorage matchingDigitalStorage = null;
        if (!digitalStorageList.isEmpty()) {
            matchingDigitalStorage = digitalStorageList.stream().toList().get(0);
        }
        if (matchingDigitalStorage != null) {
            List<Long> allowedUser = sharedFlatService.findById(
                    matchingDigitalStorage.getSharedFlat().getId(),
                    jwt
                ).getUsers().stream()
                .map(ApplicationUser::getId)
                .toList();

            authorization.authenticateUser(
                allowedUser,
                "The given digital storage does not belong to the user's shared flat!"
            );

            return matchingDigitalStorage.getStorageId();
        } else {
            return null;
        }
    }
}
