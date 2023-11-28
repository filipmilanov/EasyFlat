package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.DigitalStorageMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlwaysInStockItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemOrderType;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.repository.DigitalStorageRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.DigitalStorageValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service

public class DigitalStorageServiceImpl implements DigitalStorageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DigitalStorageRepository digitalStorageRepository;
    private final DigitalStorageMapper digitalStorageMapper;
    private final DigitalStorageValidator digitalStorageValidator;

    public DigitalStorageServiceImpl(DigitalStorageRepository digitalStorageRepository, DigitalStorageMapper digitalStorageMapper, DigitalStorageValidator digitalStorageValidator) {
        this.digitalStorageRepository = digitalStorageRepository;
        this.digitalStorageMapper = digitalStorageMapper;
        this.digitalStorageValidator = digitalStorageValidator;
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
    public List<Item> searchItems(Long id, ItemSearchDto searchItem) {
        LOGGER.trace("searchItems({}, {})", id, searchItem);
        Class alwaysInStock = null;
        if (searchItem.alwaysInStock() == null || !searchItem.alwaysInStock()) {
            alwaysInStock = Item.class;
        } else {
            alwaysInStock = AlwaysInStockItem.class;
        }


        List<Item> allItems = digitalStorageRepository.searchItems(
            id,
            (searchItem != null) ? searchItem.productName() : null,
            (searchItem != null) ? searchItem.brand() : null,
            (searchItem != null) ? searchItem.expireDateStart() : null,
            (searchItem != null) ? searchItem.expireDateEnd() : null,
            (searchItem != null) ? searchItem.fillLevel() : null,
            alwaysInStock
        );
        return allItems.stream().sorted(itemComparator(searchItem)).toList();
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
    public DigitalStorage create(DigitalStorageDto storageDto) throws ConflictException {
        LOGGER.trace("create({})", storageDto);

        digitalStorageValidator.checkDigitalStorageForCreate(storageDto);

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
}
