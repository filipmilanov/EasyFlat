package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.DigitalStorageMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlwaysInStockItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemOrderType;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.DigitalStorageRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.DigitalStorageValidator;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Comparator;
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

    public DigitalStorageServiceImpl(DigitalStorageRepository digitalStorageRepository, DigitalStorageMapper digitalStorageMapper, DigitalStorageValidator digitalStorageValidator, Validator validator) {
        this.digitalStorageRepository = digitalStorageRepository;
        this.digitalStorageMapper = digitalStorageMapper;
        this.digitalStorageValidator = digitalStorageValidator;
        this.validator = validator;
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
    public List<ItemListDto> searchItems(Long id, ItemSearchDto searchItem) throws ValidationException {
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

        Map<String, Long[]> items = new HashMap<>();
        Map<String, String> itemUnits = new HashMap<>();
        for (Item item : allItems) {
            itemUnits.computeIfAbsent(item.getGeneralName(), k -> item.getUnit());
            long currentQ = 0;
            if (items.get(item.getGeneralName()) != null) {
                currentQ = items.get(item.getGeneralName())[0];
            }
            Long[] quantityStorId = new Long[2];
            quantityStorId[0] = currentQ + item.getQuantityCurrent();
            quantityStorId[1] = item.getStorage().getStorId();
            items.put(item.getGeneralName(), quantityStorId);
        }
        List<ItemListDto> toRet = new LinkedList<>();
        for (Map.Entry<String, Long[]> item : items.entrySet()) {
            toRet.add(new ItemListDto(item.getKey(), item.getValue()[0], item.getValue()[1], itemUnits.get(item.getKey())));
        }

        return toRet;
    }

    private static Comparator<Item> itemComparator(ItemSearchDto searchItem) {
        return (item1, item2) -> {
            if (searchItem.orderType() == null) {
                return 0;
            }
            if (searchItem.orderType() == ItemOrderType.EXPIRE_DATE) {
                if (item1.getExpireDate() == null) {
                    return -1;
                }
                if (item2.getExpireDate() == null) {
                    return 1;
                }
                return item1.getExpireDate().compareTo(item2.getExpireDate());
            } else if (searchItem.orderType() == ItemOrderType.QUANTITY_CURRENT) {
                if (item1.getQuantityCurrent() == null) {
                    return -1;
                }
                if (item2.getQuantityCurrent() == null) {
                    return 1;
                }
                return item1.getQuantityCurrent().compareTo(item2.getQuantityCurrent());
            } else {
                if (item1.getProductName() == null) {
                    return -1;
                }
                if (item2.getProductName() == null) {
                    return 1;
                }
                return item2.getProductName().compareTo(item1.getProductName());
            }
        };
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
    public List<Item> getItemWithGeneralName(String name, Long storId) {
        return digitalStorageRepository.getItemWithGeneralName(storId, name);
    }
}
