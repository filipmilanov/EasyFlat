package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.DigitalStorageMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlwaysInStockItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemOrderType;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.DigitalStorageRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import at.ac.tuwien.sepr.groupphase.backend.service.UnitService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.DigitalStorageValidator;
import jakarta.validation.Validator;
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
    private final DigitalStorageMapper digitalStorageMapper;
    private final DigitalStorageValidator digitalStorageValidator;
    private final Validator validator;
    private final ItemMapper itemMapper;

    private final UnitService unitService;


    public DigitalStorageServiceImpl(DigitalStorageRepository digitalStorageRepository,
                                     DigitalStorageMapper digitalStorageMapper,
                                     DigitalStorageValidator digitalStorageValidator, Validator validator, ItemMapper itemMapper, UnitService unitService) {
        this.digitalStorageRepository = digitalStorageRepository;
        this.digitalStorageMapper = digitalStorageMapper;
        this.digitalStorageValidator = digitalStorageValidator;
        this.validator = validator;
        this.itemMapper = itemMapper;
        this.unitService = unitService;
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
    public List<DigitalStorage> findAll(DigitalStorageSearchDto digitalStorageSearchDto) {
        LOGGER.trace("findAll({})", digitalStorageSearchDto);
        return digitalStorageRepository.findByTitleContaining(
            (digitalStorageSearchDto != null) ? digitalStorageSearchDto.title() : ""
        );
    }

    @Override
    public List<Item> findAllItemsOfStorage(Long id) {
        Optional<DigitalStorage> optionalStorage = digitalStorageRepository.findById(id);
        if (optionalStorage.isPresent()) {
            return optionalStorage.get().getItemList();
        } else {
            return Collections.emptyList();
        }

    }

    @Override
    public List<Item> findAllItemsOfStorageOrdered(Long id, ItemOrderType orderType) {
        return null;
    }

    @Override
    public List<ItemListDto> searchItems(Long id, ItemSearchDto searchItem) throws ValidationException, ConflictException {
        LOGGER.trace("searchItems({}, {})", id, searchItem);
        digitalStorageValidator.validateForSearchItems(id, searchItem);
        Class alwaysInStock = null;
        if (searchItem.alwaysInStock() == null || !searchItem.alwaysInStock()) {
            alwaysInStock = Item.class;
        } else {
            alwaysInStock = AlwaysInStockItem.class;
        }

        List<Item> allItems = digitalStorageRepository.searchItems(
            id,
            (searchItem.productName() != null) ? searchItem.productName() : null,
            (searchItem.fillLevel() != null) ? searchItem.fillLevel() : null,
            alwaysInStock
        );

        List<ItemListDto> groupedItems = prepareListItemsForStorage(allItems);
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

    @Override
    public DigitalStorage create(DigitalStorageDto storageDto) throws ConflictException, at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException {
        LOGGER.trace("create({})", storageDto);

        digitalStorageValidator.validateForCreate(storageDto);

        DigitalStorage storage = digitalStorageMapper.dtoToEntity(storageDto);

        return digitalStorageRepository.save(storage);
    }

    @Override
    public DigitalStorage update(DigitalStorageDto storage) {
        return null;
    }

    @Override
    public void remove(Long id) {

    }

    @Override
    public Item updateItemQuantity(long storageId, long itemId, long quantity) {
        LOGGER.trace("updateItemQuantity({}, {}, {})", storageId, itemId, quantity);

        return digitalStorageRepository.updateItemQuantity(storageId, itemId, quantity);
    }


    @Override
    public List<ItemDto> getItemWithGeneralName(String name, Long storId) {
        List<Item> items = digitalStorageRepository.getItemWithGeneralName(storId, name);


        List<ItemDto> itemDtos = items.stream()
            .map(itemMapper::entityToDto)
            .toList();

        return itemDtos;
    }


    private List<ItemListDto> prepareListItemsForStorage(List<Item> allItems) throws ValidationException, ConflictException {
        Map<String, Double[]> items = new HashMap<>();
        Map<String, Unit> itemUnits = new HashMap<>();
        Unit unit = null;
        for (Item item : allItems) {
            itemUnits.computeIfAbsent(item.getGeneralName(), k -> item.getUnit());

            double currentQ = 0;
            double totalQ = 0;
            if (items.get(item.getGeneralName()) != null) {
                currentQ = items.get(item.getGeneralName())[0];
                totalQ = items.get(item.getGeneralName())[2];
            }

            Double updatedQuantityCurrent = unitService.convertUnits(item.getUnit(), itemUnits.get(item.getGeneralName()), item.getQuantityCurrent());
            Double updatedQuantityTotal = unitService.convertUnits(item.getUnit(), itemUnits.get(item.getGeneralName()), item.getQuantityTotal());


            Double[] quantityStorId = new Double[3];
            quantityStorId[0] = currentQ + updatedQuantityCurrent;
            quantityStorId[1] = item.getStorage().getStorId().doubleValue();
            quantityStorId[2] = totalQ + updatedQuantityTotal;
            items.put(item.getGeneralName(), quantityStorId);
        }
        List<ItemListDto> toRet = new LinkedList<>();
        for (Map.Entry<String, Double[]> item : items.entrySet()) {
            toRet.add(new ItemListDto(item.getKey(), item.getValue()[0], item.getValue()[2], item.getValue()[1].longValue(), UnitDtoBuilder.builder().name(itemUnits.get(item.getKey()).getName()).build()));
        }
        return toRet;
    }

}

