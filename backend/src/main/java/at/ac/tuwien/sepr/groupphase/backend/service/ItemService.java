package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemFieldSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;

/**
 * Service for working with Items.
 */
public interface ItemService {

    /**
     * Search for an item in the database with given ID.
     *
     * @param id a valid ID
     * @return if the id exists in the DB, a persisted DigitalStorageItem with given ID, a not found exception otherwise
     * @throws AuthenticationException if the user is not authenticated
     */
    DigitalStorageItem findById(Long id) throws AuthenticationException;

    /**
     * Search for an item in the database where one field is matching.
     *
     * @param itemFieldSearchDto fields to search for
     * @return a list of items matching the search criteria
     */
    List<DigitalStorageItem> findByFields(ItemFieldSearchDto itemFieldSearchDto);

    /**
     * Retrieves a list of items with a specific general name.
     *
     * @param generalName The general name to filter items by.
     * @return A list of items with the specified general name.
     */
    List<DigitalStorageItem> getItemWithGeneralName(String generalName);

    /**
     * Validates and Creates a new {@link DigitalStorageItem} in the db.
     *
     * @param item a storage without ID
     * @return an object of type {@link DigitalStorageItem} which is persisted and has an ID
     * @throws AuthenticationException if the user is not authenticated
     * @throws ValidationException if the item does not follow the validation given in the ItemDto
     * @throws ConflictException if AIS or storage does is not specified
     */
    DigitalStorageItem create(ItemDto item) throws AuthenticationException, ValidationException, ConflictException;

    /**
     * Validates and Updates a new {@link DigitalStorageItem} in the db.
     *
     * @param item a storage with existing ID
     * @return an object of type {@link DigitalStorageItem} which is updated
     * @throws AuthenticationException if the user is not authenticated
     * @throws ValidationException if the item does not follow the validation given in the ItemDto
     * @throws ConflictException if AIS or storage does is not specified
     */
    DigitalStorageItem update(ItemDto item) throws AuthenticationException, ValidationException, ConflictException;

    /**
     * Removes an {@link DigitalStorageItem} stored in the db.
     *
     * @param id an ID of a stored {@link DigitalStorageItem}
     * @throws AuthenticationException if the user is not authenticated
     */
    void delete(Long id) throws AuthenticationException;

    /**
     * Searches for items in the database with names matching the specified name.
     *
     * @param name The name to search for.
     * @return A list of ItemDto objects with names matching the search criteria.
     */
    List<DigitalStorageItem> findByName(String name);
}
