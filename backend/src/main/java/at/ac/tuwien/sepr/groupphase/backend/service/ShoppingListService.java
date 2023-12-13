package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;

import java.util.List;
import java.util.Optional;

public interface ShoppingListService {

    /**
     * Validates and Creates a new {@link ShoppingItem} in the db.
     *
     * @param itemDto a shopping item without ID
     * @return an object of type {@link ShoppingItem} which is persisted and has an ID
     */
    ShoppingItem create(ShoppingItemDto itemDto);

    /**
     * Search for a shopping item in the database with given ID.
     *
     * @param itemId a valid ID
     * @return if the id exists in the DB, an Optional of a persisted ShoppingItem with given ID, an empty Optional otherwise
     */
    Optional<ShoppingItem> getById(Long itemId);

    /**
     * Search for a shopping list in the database with given ID.
     *
     * @param id a valid ID
     * @return if the id exists in the DB, an Optional of a persisted ShoppingList with given ID, an empty Optional otherwise
     */
    Optional<ShoppingList> getShoppingListById(Long id);

    /**
     * Search for a shopping list in the database with given ID.
     *
     * @param listId a valid ID of a ShoppingList
     * @param itemSearchDto search parameters consisting of the product's name and its label's value
     * @return if the id exists in the DB, a List of a persisted ShoppingItems with the given ID, an empty Optional otherwise
     */
    List<ShoppingItem> getItemsById(Long listId, ShoppingItemSearchDto itemSearchDto);

    ShoppingList createList(String listName);

    ShoppingItem deleteItem(Long itemId);

    ShoppingList deleteList(Long shopId);

    List<ShoppingList> getShoppingLists(String jwt) throws AuthenticationException;

    List<Item> transferToServer(List<ShoppingItemDto> items);

    /**
     * Validates and Updates a new {@link ShoppingItem} in the db.
     *
     * @param shoppingItemDto a DTO of type shopping item with existing ID
     * @return an object of type {@link ShoppingItem} which is updated
     */
    ShoppingItem update(ShoppingItemDto shoppingItemDto) throws ConflictException;
}
