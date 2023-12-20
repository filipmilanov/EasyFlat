package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemFieldSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemStats;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemStatsRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import at.ac.tuwien.sepr.groupphase.backend.service.IngredientService;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemService;
import at.ac.tuwien.sepr.groupphase.backend.service.UnitService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.authenticator.Authorization;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.ItemValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ItemRepository itemRepository;
    private final DigitalStorageService digitalStorageService;
    private final IngredientService ingredientService;
    private final ItemMapper itemMapper;
    private final ItemValidator itemValidator;
    private final ItemStatsRepository itemStatsRepository;
    private final Authorization authorization;
    private final SharedFlatService sharedFlatService;
    private final UnitService unitService;

    public ItemServiceImpl(ItemRepository itemRepository,
                           DigitalStorageService digitalStorageService,
                           IngredientService ingredientService,
                           ItemMapper itemMapper,
                           ItemValidator itemValidator,
                           ItemStatsRepository itemStatsRepository,
                           Authorization authorization,
                           SharedFlatService sharedFlatService, UnitService unitService) {
        this.itemRepository = itemRepository;
        this.digitalStorageService = digitalStorageService;
        this.ingredientService = ingredientService;
        this.itemMapper = itemMapper;
        this.itemValidator = itemValidator;
        this.itemStatsRepository = itemStatsRepository;
        this.authorization = authorization;
        this.sharedFlatService = sharedFlatService;
        this.unitService = unitService;
    }

    @Override
    public Item findById(Long id, String jwt) throws AuthenticationException {
        LOGGER.trace("findById({})", id);
        if (id == null) {
            throw new NotFoundException("Item ID can't be null");
        }

        Optional<DigitalStorageItem> item = itemRepository.findById(id);

        if (item.isEmpty()) {
            throw new NotFoundException("Item could not be found");
        } else {

        List<Long> allowedUser = item.get().getDigitalStorage().getSharedFlat().getUsers().stream().map(ApplicationUser::getId).toList();
        authorization.authenticateUser(
                jwt,
                allowedUser,
                "The given item does not belong to the user's shared flat!"
            );

            return item.get();
        }
    }

    @Override
    public List<DigitalStorageItem> findByFields(ItemFieldSearchDto itemFieldSearchDto) {
        LOGGER.trace("findByFields({})", itemFieldSearchDto);

        return itemRepository.findAllByItemCache_GeneralNameContainingIgnoreCaseOrItemCache_BrandContainingIgnoreCaseOrBoughtAtContainingIgnoreCase(
            itemFieldSearchDto.generalName(),
            itemFieldSearchDto.brand(),
            itemFieldSearchDto.boughtAt()
        );
    }

    @Override
    @Transactional
    public DigitalStorageItem create(ItemDto itemDto, String jwt) throws ConflictException, ValidationException, AuthenticationException {
        LOGGER.trace("create({})", itemDto);

        if (itemDto.alwaysInStock() == null) {
            itemDto = itemDto.withAlwaysInStock(false);
        }

        List<DigitalStorage> digitalStorageList = digitalStorageService.findAll(null, jwt);
        List<Unit> unitList = unitService.findAll();
        itemValidator.validateForCreate(itemDto, digitalStorageList, unitList);

        ItemDto finalItemDto = itemDto;
        DigitalStorage matchingDigitalStorage = digitalStorageList.stream()
                .filter(digitalStorage -> Objects.equals(finalItemDto.digitalStorage().storId(), digitalStorage.getStorId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Given digital storage does not exists in the Database!"));

        List<Long> allowedUser = sharedFlatService.findById(
                        matchingDigitalStorage.getSharedFlat().getId(),
                        jwt
                ).getUsers().stream()
                .map(ApplicationUser::getId)
                .toList();
        authorization.authenticateUser(
                jwt,
                allowedUser,
                "The given digital storage does not belong to the user's shared flat!"
        );


        List<Ingredient> ingredientList = ingredientService.findIngredientsAndCreateMissing(itemDto.ingredients());

        ItemStats curr = new ItemStats();
        curr.setDateOfPurchase(LocalDate.now());
        curr.setAmountSpendOn(itemDto.priceInCent());
        curr.setItemStatId(itemDto.itemId());
        List<ItemStats> itemStats = new ArrayList<>();
        itemStats.add(curr);
        itemStatsRepository.save(curr);

        DigitalStorageItem digitalStorageItem;
        if (itemDto.alwaysInStock()) {
            digitalStorageItem = itemMapper.dtoToAlwaysInStock(itemDto, ingredientList, null);
        } else {
            digitalStorageItem = itemMapper.dtoToEntity(itemDto, ingredientList, null);
        }
        DigitalStorageItem createdDigitalStorageItem = itemRepository.save(digitalStorageItem);
        createdDigitalStorageItem.setIngredientList(ingredientList);
        return createdDigitalStorageItem;
    }

    @Override
    @Transactional
    public DigitalStorageItem update(ItemDto itemDto, String jwt) throws ConflictException, ValidationException, AuthenticationException {
        LOGGER.trace("update({})", itemDto);

        if (itemDto.alwaysInStock() == null) {
            itemDto = itemDto.withAlwaysInStock(false);
        }

        List<DigitalStorage> digitalStorageList = digitalStorageService.findAll(null, jwt);
        List<Unit> unitList = unitService.findAll();
        itemValidator.validateForUpdate(itemDto, digitalStorageList, unitList);

        ItemDto finalItemDto = itemDto;
        DigitalStorage matchingDigitalStorage = digitalStorageList.stream()
                .filter(digitalStorage -> Objects.equals(finalItemDto.digitalStorage().storId(), digitalStorage.getStorId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Given digital storage does not exists in the Database!"));

        List<Long> allowedUser = sharedFlatService.findById(
                        matchingDigitalStorage.getSharedFlat().getId(),
                        jwt
                ).getUsers().stream()
                .map(ApplicationUser::getId)
                .toList();
        authorization.authenticateUser(
                jwt,
                allowedUser,
                "The given digital storage does not belong to the user's shared flat!"
        );

        List<Ingredient> ingredientList = ingredientService.findIngredientsAndCreateMissing(itemDto.ingredients());

        DigitalStorageItem digitalStorageItem;
        if (itemDto.alwaysInStock()) {
            digitalStorageItem = itemMapper.dtoToAlwaysInStock(itemDto, ingredientList, null);
        } else {
            digitalStorageItem = itemMapper.dtoToEntity(itemDto, ingredientList, null);
        }

        Item presistedItem = this.findById(itemDto.itemId(), jwt);

        // necessary because JPA cannot convert an Entity to another Entity
        if (digitalStorageItem.alwaysInStock() != presistedDigitalStorageItem.alwaysInStock()) {
            this.delete(itemDto.itemId(), jwt);
        }

        DigitalStorageItem updatedDigitalStorageItem = itemRepository.save(digitalStorageItem);
        updatedDigitalStorageItem.setIngredientList(ingredientList);
        return updatedDigitalStorageItem;
    }

    @Override
    public void delete(Long id, String jwt) throws AuthenticationException {
        LOGGER.trace("delete({})", id);

        Item itemToDelete = this.findById(id, jwt);

        Long sharedFlatId = digitalStorageItemToDelete.getDigitalStorage().getSharedFlat().getId();

        List<Long> allowedUsers = sharedFlatService.findById(sharedFlatId, jwt)
                .getUsers().stream()
                .map(ApplicationUser::getId)
                .toList();

        authorization.authenticateUser(
                jwt,
                allowedUsers,
                "The given digital storage does not belong to the user's shared flat!"
        );

        itemRepository.deleteById(id);
    }
}
