package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import at.ac.tuwien.sepr.groupphase.backend.service.IngredientService;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.ItemValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
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

    public ItemServiceImpl(ItemRepository itemRepository, DigitalStorageService digitalStorageService, IngredientService ingredientService, ItemMapper itemMapper, ItemValidator itemValidator) {
        this.itemRepository = itemRepository;
        this.digitalStorageService = digitalStorageService;
        this.ingredientService = ingredientService;
        this.itemMapper = itemMapper;
        this.itemValidator = itemValidator;
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
    public Item create(ItemDto itemDto) throws ConflictException {
        LOGGER.trace("create({})", itemDto);

        Optional<DigitalStorage> digitalStorage = digitalStorageService.findById(itemDto.storageId());
        if (digitalStorage.isEmpty()) {
            throw new ConflictException("Cannot process given entity", List.of("Digital Storage does not exists"));
        }
        itemValidator.checkItemForCreate(itemDto, digitalStorage.get());
        List<Ingredient> ingredientList = ingredientService.findAllByIds(itemDto.ingredientsIdList());
        Item item = itemMapper.dtoToItem(itemDto, digitalStorage.get(), ingredientList);

        return itemRepository.save(item);
    }

    @Override
    public Item update(ItemDto item) {
        return null;
    }

    @Override
    public void remove(Long id) {

    }
}
