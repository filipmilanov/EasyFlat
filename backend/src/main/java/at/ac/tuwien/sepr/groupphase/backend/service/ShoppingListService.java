package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
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
     * @throws AuthenticationException If authentication fails or the user does not exist
     */
    ShoppingItem createShoppingItem(ShoppingItemDto itemDto) throws AuthenticationException, ValidationException, ConflictException, AuthorizationException;

    /**
     * Search for a shopping item in the database with given ID.
     *
     * @param itemId a valid ID
     * @return if the id exists in the DB, an Optional of a persisted ShoppingItem with given ID, an empty Optional otherwise
     */
    Optional<ShoppingItem> getById(Long itemId) throws AuthenticationException;

    /**
     * Search for a shopping list in the database with given ID.
     *
     * @param name a valid listName
     * @return if the id exists in the DB, an Optional of a persisted ShoppingList with given ID, an empty Optional otherwise
     * @throws AuthenticationException If authentication fails or the user does not exist
     */
    Optional<ShoppingList> getShoppingListByName(String name) throws AuthenticationException;

    /**
     * Search for a shopping list in the database with given ID.
     *
     * @param id a valid ID of a ShoppingList
     * @return if the id exists in the DB, an Optional of a persisted ShoppingList with given ID, an empty Optional otherwise
     * @throws AuthenticationException If authentication fails or the user does not exist
     */
    Optional<ShoppingList> getShoppingListById(Long id) throws AuthenticationException;


    /**
     * Search for a shopping list in the database with given ID.
     *
     * @param id a valid ID of a ShoppingList
     * @param itemSearchDto search parameters consisting of the product's name and its label's value
     * @return if the id exists in the DB, a List of a persisted ShoppingItems with the given ID, an empty Optional otherwise
     * @throws AuthenticationException If authentication fails or the user does not exist
     */
    List<ShoppingItem> getItemsById(Long id, ShoppingItemSearchDto itemSearchDto) throws AuthenticationException;

    /**
     * Create a new ShoppingList in the db.
     *
     * @param listName a valid name for the new ShoppingList
     * @return an object of type {@link ShoppingList} which is persisted and has an ID
     */
    ShoppingList createList(String listName) throws ValidationException, AuthenticationException, ConflictException;

    /**
     * Delete a ShoppingItem from the db based on its ID.
     *
     * @param itemId a valid ID of a ShoppingItem
     * @return the deleted ShoppingItem
     */
    ShoppingItem deleteItem(Long itemId) throws AuthenticationException;

    /**
     * Delete a ShoppingList from the db based on its ID.
     *
     * @param shopId a valid ID of a ShoppingList
     * @return the deleted ShoppingList
     */
    ShoppingList deleteList(Long shopId) throws ValidationException, AuthenticationException, AuthorizationException;

    /**
     * Get all ShoppingLists from the db filtered by search parameters.
     *
     * @param searchParams name of the list, through which we search for it. Can also be null
     * @return a List of all persisted ShoppingLists
     * @throws AuthenticationException If authentication fails or the user does not exist
     */
    List<ShoppingList> getShoppingLists(String searchParams) throws AuthenticationException;

    /**
     * Transfer ShoppingItems to the server.
     *
     * @param items a List of ShoppingItemDto to be transferred
     * @return a List of DigitalStorageItem objects
     */
    List<DigitalStorageItem> transferToServer(List<ShoppingItemDto> items) throws AuthenticationException;

    /**
     * Validates and Updates a new {@link ShoppingItem} in the db.
     *
     * @param shoppingItemDto a DTO of type shopping item with existing ID
     * @return an object of type {@link ShoppingItem} which is updated
     * @throws AuthenticationException If authentication fails or the user does not exist
     * @throws ConflictException if there is a conflict with the persisted data
     * @throws ValidationException if the data in shoppingItemDto is not valid
     */
    ShoppingItem update(ShoppingItemDto shoppingItemDto)
        throws ConflictException, AuthenticationException, ValidationException, AuthorizationException;
}

