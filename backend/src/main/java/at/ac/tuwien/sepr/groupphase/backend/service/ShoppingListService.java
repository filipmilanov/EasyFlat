package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;
import java.util.Optional;

public interface ShoppingListService {

    /**
     * Validates and Creates a new {@link ShoppingItem} in the db.
     *
     * @param itemDto a shopping item without ID
     * @return an object of type {@link ShoppingItem} which is persisted and has an ID
     */
    ShoppingItem create(ShoppingItemDto itemDto, String jwt) throws AuthenticationException, ValidationException, ConflictException;

    /**
     * Search for a shopping item in the database with given ID.
     *
     * @param itemId a valid ID
     * @return if the id exists in the DB, an Optional of a persisted ShoppingItem with given ID, an empty Optional otherwise
     */
    Optional<ShoppingItem> getById(Long itemId, String jwt) throws AuthenticationException;

    /**
     * Search for a shopping list in the database with given ID.
     *
     * @param name a valid listName
     * @return if the id exists in the DB, an Optional of a persisted ShoppingList with given ID, an empty Optional otherwise
     */
    Optional<ShoppingList> getShoppingListByName(String name, String jwt) throws AuthenticationException;

    /**
     * Search for a shopping list in the database with given ID.
     *
     * @param id a valid ID of a ShoppingList
     * @return if the id exists in the DB, an Optional of a persisted ShoppingList with given ID, an empty Optional otherwise
     */
    Optional<ShoppingList> getShoppingListById(Long id, String jwt) throws AuthenticationException;


    /**
     * Search for a shopping list in the database with given ID.
     *
     * @param name a valid ID of a ShoppingList
     * @param itemSearchDto search parameters consisting of the product's name and its label's value
     * @return if the id exists in the DB, a List of a persisted ShoppingItems with the given ID, an empty Optional otherwise
     */
    List<ShoppingItem> getItemsByName(String name, ShoppingItemSearchDto itemSearchDto, String jwt) throws AuthenticationException;

    /**
     * Create a new ShoppingList in the db.
     *
     * @param listName a valid name for the new ShoppingList
     * @return an object of type {@link ShoppingList} which is persisted and has an ID
     */
    ShoppingList createList(String listName, String jwt) throws ValidationException, AuthenticationException;

    /**
     * Delete a ShoppingItem from the db based on its ID.
     *
     * @param itemId a valid ID of a ShoppingItem
     * @return the deleted ShoppingItem
     */
    ShoppingItem deleteItem(Long itemId, String jwt) throws AuthenticationException;

    /**
     * Delete a ShoppingList from the db based on its ID.
     *
     * @param shopId a valid ID of a ShoppingList
     * @return the deleted ShoppingList
     */
    ShoppingList deleteList(Long shopId, String jwt) throws ValidationException, AuthenticationException;

    /**
     * Get all ShoppingLists from the db.
     *
     * @return a List of all persisted ShoppingLists
     */
    List<ShoppingList> getShoppingLists(String jwt) throws AuthenticationException;

    /**
     * Transfer ShoppingItems to the server.
     *
     * @param items a List of ShoppingItemDto to be transferred
     * @return a List of Item objects
     */
    List<Item> transferToServer(List<ShoppingItemDto> items, String jwt) throws AuthenticationException;

    /**
     * Validates and Updates a new {@link ShoppingItem} in the db.
     *
     * @param shoppingItemDto a DTO of type shopping item with existing ID
     * @return an object of type {@link ShoppingItem} which is updated
     */
    ShoppingItem update(ShoppingItemDto shoppingItemDto, String jwt) throws ConflictException, AuthenticationException, ValidationException;
}

