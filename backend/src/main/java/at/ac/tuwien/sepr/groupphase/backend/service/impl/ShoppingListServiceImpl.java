package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.IngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShoppingListMapper;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ShoppingListServiceImpl implements ShoppingListService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ShoppingRepository shoppingRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListMapper shoppingListMapper;
    private final LabelService labelService;
    private final ItemMapper itemMapper;
    private final IngredientMapper ingredientMapper;
    private final ItemRepository itemRepository;

    public ShoppingListServiceImpl(ShoppingRepository shoppingRepository, ShoppingListRepository shoppingListRepository,
                                   ShoppingListMapper shoppingListMapper, LabelService labelService, ItemMapper itemMapper,
                                   IngredientMapper ingredientMapper, ItemRepository itemRepository) {
        this.shoppingRepository = shoppingRepository;
        this.labelService = labelService;
        this.itemMapper = itemMapper;
        this.shoppingListRepository = shoppingListRepository;
        this.shoppingListMapper = shoppingListMapper;
        this.ingredientMapper = ingredientMapper;
        this.itemRepository = itemRepository;
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

    @Override
    public ShoppingItem deleteItem(Long itemId) {
        Optional<ShoppingItem> toDeleteOptional = shoppingRepository.findById(itemId);

        if (toDeleteOptional.isPresent()) {
            ShoppingItem toDelete = toDeleteOptional.get();
            shoppingRepository.deleteById(itemId);
            return toDelete;
        } else {
            throw new NoSuchElementException("Item with this id does not exist!");
        }
    }

    @Override
    public ShoppingList deleteList(Long shopId) {
        if (shopId == 1) {
            throw new BadCredentialsException("The main list can not be deleted!");
        }
        Optional<ShoppingList> toDeleteOptional = shoppingListRepository.findById(shopId);

        if (toDeleteOptional.isPresent()) {
            ShoppingList toDelete = toDeleteOptional.get();
            List<ShoppingItem> items = shoppingRepository.findByShoppingListId(shopId);
            shoppingRepository.deleteAll(items);
            shoppingListRepository.deleteById(shopId);
            return toDelete;
        } else {
            throw new NoSuchElementException("Shopping list with this id does not exist!");
        }
    }

    @Override
    public List<ShoppingList> getShoppingLists() {
        List<ShoppingList> shoppingLists = shoppingListRepository.findAll();
        shoppingListMapper.entityListToDtoList(shoppingLists);
        return shoppingListRepository.saveAll(shoppingLists);
    }

    @Override
    public List<Item> transferToServer(List<ShoppingItemDto> items) {
        List<Item> itemsList = new ArrayList<>();
        for (ShoppingItemDto itemDto: items) {

            Item item = shoppingListMapper.shoppingItemDtoToItem(itemDto, ingredientMapper.dtoListToEntityList(itemDto.ingredients()));
            itemRepository.save(item);
            shoppingRepository.deleteById(item.getItemId());
            itemsList.add(item);
        }
        return itemsList;
    }


    private List<ItemLabel> findItemLabelsAndCreateNew(List<ItemLabelDto> labels) {
        if (labels == null) {
            return List.of();
        }
        List<ItemLabel> ret = new ArrayList<>();
        List<ItemLabelDto> newLabels = labels.stream().toList();

        if (!newLabels.isEmpty()) {
            List<ItemLabel> createdLabels = labelService.createAll(newLabels);
            ret.addAll(createdLabels);
        }
        return ret;
    }
}
