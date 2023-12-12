package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;

import java.util.List;
import java.util.Optional;

public interface ShoppingListService {
    ShoppingItem create(ShoppingItemDto itemDto);

    Optional<ShoppingItem> getById(Long itemId);

    Optional<ShoppingList> getShoppingListById(Long id);

    List<ShoppingItem> getItemsById(Long listId);

    ShoppingList createList(String listName);

    ShoppingItem deleteItem(Long itemId);

    ShoppingList deleteList(Long shopId);

    List<ShoppingList> getShoppingLists();

    List<Item> transferToServer(List<ShoppingItemDto> items);
}
