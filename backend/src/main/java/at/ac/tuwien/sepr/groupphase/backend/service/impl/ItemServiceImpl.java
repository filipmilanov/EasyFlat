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
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
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
    private final AuthService authService;
    private final Authorization authorization;
    private final UnitService unitService;

    public ItemServiceImpl(ItemRepository itemRepository,
                           DigitalStorageService digitalStorageService,
                           IngredientService ingredientService,
                           ItemMapper itemMapper,
                           ItemValidator itemValidator,
                           ItemStatsRepository itemStatsRepository,
                           AuthService authService,
                           Authorization authorization,
                           UnitService unitService) {
        this.itemRepository = itemRepository;
        this.digitalStorageService = digitalStorageService;
        this.ingredientService = ingredientService;
        this.itemMapper = itemMapper;
        this.itemValidator = itemValidator;
        this.itemStatsRepository = itemStatsRepository;
        this.authService = authService;
        this.authorization = authorization;
        this.unitService = unitService;
    }

    @Override
    public DigitalStorageItem findById(Long id) throws AuthenticationException {
        LOGGER.trace("findById({})", id);
        if (id == null) {
            throw new NotFoundException("No item ID given!");
        }

        Optional<DigitalStorageItem> item = itemRepository.findById(id);

        if (item.isEmpty()) {
            throw new NotFoundException("The given item ID could not be found in the database!");
        }
        List<Long> allowedUser = item.get().getDigitalStorage().getSharedFlat().getUsers().stream().map(ApplicationUser::getId).toList();
        authorization.authenticateUser(
            allowedUser,
            "The given item does not belong to the user's shared flat!"
        );

        return item.get();
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
    public List<DigitalStorageItem> getItemWithGeneralName(String generalName) {
        LOGGER.trace("getItemWithGeneralName({})", generalName);

        ApplicationUser user = authService.getUserFromToken();

        Long digitalStorageId = user.getSharedFlat().getDigitalStorage().getStorageId();

        return itemRepository.findAllByDigitalStorage_StorageIdAndItemCache_GeneralName(digitalStorageId, generalName);
    }

    @Override
    @Transactional
    public DigitalStorageItem create(ItemDto itemDto) throws ConflictException, ValidationException, AuthenticationException {
        LOGGER.trace("create({})", itemDto);

        ApplicationUser applicationUser = authService.getUserFromToken();
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exist"));
        }

        if (itemDto.alwaysInStock() == null) {
            itemDto = itemDto.withAlwaysInStock(false);
        }

        List<DigitalStorage> digitalStorageList = digitalStorageService.findAll(null);
        List<Unit> unitList = unitService.findAll();
        itemValidator.validateForCreate(itemDto, digitalStorageList, unitList);

        List<Long> allowedUsers = authService.getUserFromToken().getSharedFlat().getUsers().stream()
            .map(ApplicationUser::getId)
            .toList();

        authorization.authenticateUser(
            allowedUsers,
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
    public DigitalStorageItem update(ItemDto itemDto) throws ConflictException, ValidationException, AuthenticationException {
        LOGGER.trace("update({})", itemDto);

        if (itemDto.alwaysInStock() == null) {
            itemDto = itemDto.withAlwaysInStock(false);
        }

        List<DigitalStorage> digitalStorageList = digitalStorageService.findAll(null);
        List<Unit> unitList = unitService.findAll();
        itemValidator.validateForUpdate(itemDto, digitalStorageList, unitList);

        List<Long> allowedUsers = authService.getUserFromToken().getSharedFlat().getUsers().stream()
            .map(ApplicationUser::getId)
            .toList();
        authorization.authenticateUser(
            allowedUsers,
            "The given digital storage does not belong to the user's shared flat!"
        );

        List<Ingredient> ingredientList = ingredientService.findIngredientsAndCreateMissing(itemDto.ingredients());

        DigitalStorageItem digitalStorageItem;
        if (itemDto.alwaysInStock()) {
            digitalStorageItem = itemMapper.dtoToAlwaysInStock(itemDto, ingredientList, null);
        } else {
            digitalStorageItem = itemMapper.dtoToEntity(itemDto, ingredientList, null);
        }

        DigitalStorageItem presistedDigitalStorageItem = this.findById(itemDto.itemId());

        // necessary because JPA cannot convert an Entity to another Entity
        if (digitalStorageItem.alwaysInStock() != presistedDigitalStorageItem.alwaysInStock()) {
            this.delete(itemDto.itemId());
        }

        DigitalStorageItem updatedDigitalStorageItem = itemRepository.save(digitalStorageItem);
        updatedDigitalStorageItem.setIngredientList(ingredientList);
        return updatedDigitalStorageItem;
    }

    @Override
    @Transactional
    public void delete(Long id) throws AuthenticationException {
        LOGGER.trace("delete({})", id);

        List<Long> allowedUsers = authService.getUserFromToken().getSharedFlat().getUsers().stream()
            .map(ApplicationUser::getId)
            .toList();

        authorization.authenticateUser(
            allowedUsers,
            "The given digital storage does not belong to the user's shared flat!"
        );

        itemRepository.deleteById(id);
    }
}
