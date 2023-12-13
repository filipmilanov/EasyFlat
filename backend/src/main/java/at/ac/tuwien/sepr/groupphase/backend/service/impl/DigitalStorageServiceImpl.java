package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.DigitalStorageMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SharedFlatMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlwaysInStockItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemOrderType;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.DigitalStorageRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.authenticator.Authenticator;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.DigitalStorageValidator;
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
    private final SharedFlatService sharedFlatService;
    private final Authenticator authenticator;
    private CustomUserDetailService customUserDetailService;

    private SharedFlatMapper sharedFlatMapper;

    public DigitalStorageServiceImpl(DigitalStorageRepository digitalStorageRepository,
                                     DigitalStorageMapper digitalStorageMapper,
                                     DigitalStorageValidator digitalStorageValidator,
                                     SharedFlatService sharedFlatService,
                                     Authenticator authenticator,
                                     CustomUserDetailService customUserDetailService,
                                     SharedFlatMapper sharedFlatMapper) {
        this.digitalStorageRepository = digitalStorageRepository;
        this.digitalStorageMapper = digitalStorageMapper;
        this.digitalStorageValidator = digitalStorageValidator;
        this.sharedFlatService = sharedFlatService;
        this.authenticator = authenticator;
        this.customUserDetailService = customUserDetailService;
        this.sharedFlatMapper = sharedFlatMapper;
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
    public List<DigitalStorage> findAll(DigitalStorageSearchDto digitalStorageSearchDto, String jwt) throws AuthenticationException {
        LOGGER.trace("findAll({})", digitalStorageSearchDto);

        ApplicationUser applicationUser = customUserDetailService.getUser(jwt);
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
    public List<ItemListDto> searchItems(ItemSearchDto searchItem, String jwt) throws ValidationException, AuthenticationException, ConflictException {
        LOGGER.trace("searchItems({}, {})", searchItem);
        digitalStorageValidator.validateForSearchItems(searchItem);

        Long storId = getStorIdForUser(jwt);


        Class alwaysInStock = null;
        if (searchItem.alwaysInStock() == null || !searchItem.alwaysInStock()) {
            alwaysInStock = Item.class;
        } else {
            alwaysInStock = AlwaysInStockItem.class;
        }

        List<Item> allItems = digitalStorageRepository.searchItems(
            storId,
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
    public DigitalStorage create(DigitalStorageDto storageDto, String jwt) throws ConflictException, ValidationException, AuthenticationException {
        LOGGER.trace("create({})", storageDto);


        digitalStorageValidator.validateForCreate(storageDto);

        List<Long> allowedUser = sharedFlatService.findById(
                storageDto.sharedFlat().getId(),
                jwt
            ).getUsers().stream()
            .map(ApplicationUser::getId)
            .toList();
        authenticator.authenticateUser(
            jwt,
            allowedUser,
            "The given digital storage does not belong to the user's shared flat!"
        );


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
    public List<Item> getItemWithGeneralName(String name, String jwt) throws AuthenticationException, ValidationException, ConflictException {
        Long storId = getStorIdForUser(jwt);
        return digitalStorageRepository.getItemWithGeneralName(storId, name);
    }

    private List<ItemListDto> prepareListItemsForStorage(List<Item> allItems) {
        Map<String, Long[]> items = new HashMap<>();
        Map<String, String> itemUnits = new HashMap<>();
        for (Item item : allItems) {
            itemUnits.computeIfAbsent(item.getGeneralName(), k -> item.getUnit());
            long currentQ = 0;
            long totalQ = 0;
            if (items.get(item.getGeneralName()) != null) {
                currentQ = items.get(item.getGeneralName())[0];
                totalQ = items.get(item.getGeneralName())[2];
            }
            Long[] quantityStorId = new Long[3];
            quantityStorId[0] = currentQ + item.getQuantityCurrent();
            quantityStorId[1] = item.getStorage().getStorId();
            quantityStorId[2] = totalQ + item.getQuantityTotal();
            items.put(item.getGeneralName(), quantityStorId);
        }
        List<ItemListDto> toRet = new LinkedList<>();
        for (Map.Entry<String, Long[]> item : items.entrySet()) {
            toRet.add(new ItemListDto(item.getKey(), item.getValue()[0], item.getValue()[2], item.getValue()[1], itemUnits.get(item.getKey())));
        }
        return toRet;
    }

    /**
     * The Method assume, that there is only one storage per sharedFlat.
     */
    private Long getStorIdForUser(String jwt) throws AuthenticationException, ValidationException, ConflictException {
        List<DigitalStorage> digitalStorageList = findAll(null, jwt);
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


            authenticator.authenticateUser(
                jwt,
                allowedUser,
                "The given digital storage does not belong to the user's shared flat!"
            );


            return matchingDigitalStorage.getStorId();
        } else {
            ApplicationUser applicationUser = customUserDetailService.getUser(jwt);
            DigitalStorageDto storageDto = new DigitalStorageDto(null, "Storage", sharedFlatMapper.entityToWgDetailDto(applicationUser.getSharedFlat()));
            DigitalStorage newStorage = create(storageDto, jwt);

            return newStorage.getStorId();
        }
    }


}
