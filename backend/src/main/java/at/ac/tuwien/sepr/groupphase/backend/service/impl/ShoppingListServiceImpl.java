package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShoppingListMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemLabel;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.LabelService;
import at.ac.tuwien.sepr.groupphase.backend.service.ShoppingListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Service
public class ShoppingListServiceImpl implements ShoppingListService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ShoppingRepository shoppingRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListMapper shoppingListMapper;
    private final LabelService labelService;
    private final ItemMapper itemMapper;


    public ShoppingListServiceImpl(ShoppingRepository shoppingRepository, ShoppingListRepository shoppingListRepository,
                                   ShoppingListMapper shoppingListMapper, LabelService labelService, ItemMapper itemMapper) {
        this.shoppingRepository = shoppingRepository;
        this.labelService = labelService;
        this.itemMapper = itemMapper;
        this.shoppingListRepository = shoppingListRepository;
        this.shoppingListMapper = shoppingListMapper;
    }

    @Override
    public ShoppingItem create(ShoppingItemDto itemDto) {
        List<ItemLabel> labels = findItemLabelsAndCreateNew(itemDto.labels());

        ShoppingItem createdItem = shoppingRepository.save(itemMapper.dtoToShopping(itemDto, labels, shoppingListMapper.dtoToEntity(itemDto.shoppingList())));
        createdItem.setLabels(labels);
        return createdItem;
    }

    @Override
    public Optional<ShoppingItem> getById(Long itemId) {
        LOGGER.trace("findById({})", itemId);
        if (itemId == null) {
            return Optional.empty();
        }

        return shoppingRepository.findById(itemId);
    }

    @Override
    public Optional<ShoppingList> getShoppingListById(Long shopListId) {
        if (shopListId == null) {
            return Optional.empty();
        }

        return shoppingListRepository.getByShopListId(shopListId);
    }

    @Override
    public List<ShoppingItem> getItemsById(Long listId) {
        List<ShoppingItem> shoppingItems = shoppingRepository.findByShoppingListId(listId);
        itemMapper.shoppingItemListToShoppingDto(shoppingItems);
        return shoppingRepository.saveAll(shoppingItems);
    }

    @Override
    public ShoppingList createList(String listName) {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setName(listName);
        return shoppingListRepository.save(shoppingList);
    }


    private List<ItemLabel> findItemLabelsAndCreateNew(List<ItemLabelDto> labels) {
        if (labels == null) {
            return List.of();
        }
        List<ItemLabel> ret = labelService.findByValue(
            labels.stream()
                .map(ItemLabelDto::labelValue)
                .toList()
        );
        List<ItemLabelDto> newLabels = labels.stream()
            .filter(labelDto ->
                ret.stream()
                    .noneMatch(label ->
                        label.getLabelValue().equals(labelDto.labelValue())
                    )
            ).toList();

        if (!newLabels.isEmpty()) {
            List<ItemLabel> createdLabels = labelService.createAll(newLabels);
            ret.addAll(createdLabels);
        }
        return ret;
    }
}
