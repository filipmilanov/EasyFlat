package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemStats;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemStatsRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import at.ac.tuwien.sepr.groupphase.backend.service.IngredientService;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemService;
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
    private final CustomUserDetailService customUserDetailService;

    public ItemServiceImpl(ItemRepository itemRepository, DigitalStorageService digitalStorageService, IngredientService ingredientService, ItemMapper itemMapper, ItemValidator itemValidator, ItemStatsRepository itemStatsRepository, CustomUserDetailService customUserDetailService) {
        this.itemRepository = itemRepository;
        this.digitalStorageService = digitalStorageService;
        this.ingredientService = ingredientService;
        this.itemMapper = itemMapper;
        this.itemValidator = itemValidator;
        this.itemStatsRepository = itemStatsRepository;
        this.customUserDetailService = customUserDetailService;
    }

    @Override
    public Optional<Item> findById(Long id) {
        LOGGER.trace("findById({})", id);
        if (id == null) {
            return Optional.empty();
        }

        return itemRepository.findById(id);
    }

    @Override
    @Transactional
    public Item create(ItemDto itemDto, String jwt) throws ConflictException, ValidationException, AuthenticationException {
        LOGGER.trace("create({})", itemDto);

        if (itemDto.alwaysInStock() == null) {
            itemDto = itemDto.withAlwaysInStock(false);
        }

        ApplicationUser user = customUserDetailService.getUser(jwt);
        if (!Objects.equals(user.getSharedFlat().getDigitalStorage().getStorId(), itemDto.digitalStorage().storId())) {
            throw new AuthenticationException("Authentication Issue", List.of("The given digital storage does not belong to the user's shared flat!"));
        }

        List<DigitalStorage> digitalStorageList = digitalStorageService.findAll(null);
        itemValidator.validateForCreate(itemDto, digitalStorageList);

        List<Ingredient> ingredientList = findIngredientsAndCreateMissing(itemDto.ingredients());

        ItemStats curr = new ItemStats();
        curr.setDateOfPurchase(LocalDate.now());
        curr.setAmountSpendOn(itemDto.priceInCent());
        curr.setItemStatId(itemDto.itemId());
        List<ItemStats> itemStats = new ArrayList<>();
        itemStats.add(curr);
        itemStatsRepository.save(curr);

        Item item;
        if (itemDto.alwaysInStock()) {
            item = itemMapper.dtoToAlwaysInStock(itemDto, ingredientList, null);
        } else {
            item = itemMapper.dtoToEntity(itemDto, ingredientList, null);
        }
        Item createdItem = itemRepository.save(item);
        createdItem.setIngredientList(ingredientList);
        return createdItem;
    }

    @Override
    @Transactional
    public Item update(ItemDto itemDto) throws ConflictException, ValidationException {
        LOGGER.trace("update({})", itemDto);

        if (itemDto.alwaysInStock() == null) {
            itemDto = itemDto.withAlwaysInStock(false);
        }

        List<DigitalStorage> digitalStorageList = digitalStorageService.findAll(null);
        Item presistedItem = this.findById(itemDto.itemId()).orElseThrow(() -> new NotFoundException("Given Id does not exists in the Database!"));
        itemValidator.validateForUpdate(itemDto, digitalStorageList);

        List<Ingredient> ingredientList = findIngredientsAndCreateMissing(itemDto.ingredients());

        Item item;
        if (itemDto.alwaysInStock()) {
            item = itemMapper.dtoToAlwaysInStock(itemDto, ingredientList, null);
        } else {
            item = itemMapper.dtoToEntity(itemDto, ingredientList, null);
        }

        // necessary because JPA cannot convert an Entity to another Entity
        if (item.alwaysInStock() != presistedItem.alwaysInStock()) {
            this.delete(itemDto.itemId());
        }

        Item updatedItem = itemRepository.save(item);
        updatedItem.setIngredientList(ingredientList);
        return updatedItem;
    }

    @Override
    public void delete(Long id) {
        LOGGER.trace("delete({})", id);

        itemRepository.deleteById(id);
    }

    private List<Ingredient> findIngredientsAndCreateMissing(List<IngredientDto> ingredientDtoList) throws ConflictException {
        if (ingredientDtoList == null) {
            return List.of();
        }
        List<Ingredient> ingredientList = ingredientService.findByTitle(
            ingredientDtoList.stream()
                .map(IngredientDto::name)
                .toList()
        );

        List<IngredientDto> missingIngredients = ingredientDtoList.stream()
            .filter(ingredientDto ->
                ingredientList.stream()
                    .noneMatch(ingredient ->
                        ingredient.getTitle().equals(ingredientDto.name())
                    )
            ).toList();

        if (!missingIngredients.isEmpty()) {
            List<Ingredient> createdIngredients = ingredientService.createAll(missingIngredients);
            ingredientList.addAll(createdIngredients);
        }
        return ingredientList;
    }
}
