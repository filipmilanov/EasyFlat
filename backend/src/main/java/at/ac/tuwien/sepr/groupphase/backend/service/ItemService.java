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
     * @param jwt a valid JWT of a user
     * @throws AuthenticationException if the user is not authenticated
     * @return if the id exists in the DB, a persisted DigitalStorageItem with given ID, a not found exception otherwise
     */
    DigitalStorageItem findById(Long id, String jwt) throws AuthenticationException;

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
     * @param jwt  A valid JWT token for user authentication.
     * @return A list of items with the specified general name.
     */
    List<DigitalStorageItem> getItemWithGeneralName(String generalName, String jwt);

    /**
     * Validates and Creates a new {@link DigitalStorageItem} in the db.
     *
     * @param item a storage without ID
     * @param jwt  a valid JWT of a user
     * @return an object of type {@link DigitalStorageItem} which is persisted and has an ID
     * @throws AuthenticationException if the user is not authenticated
     * @throws ValidationException if the item does not follow the validation given in the ItemDto
     * @throws ConflictException if AIS or storage does is not specified
     */
    DigitalStorageItem create(ItemDto item, String jwt) throws ConflictException, ValidationException, AuthenticationException;

    /**
     * Validates and Updates a new {@link DigitalStorageItem} in the db.
     *
     * @param item a storage with existing ID
     * @param jwt a valid JWT of a user
     * @return an object of type {@link DigitalStorageItem} which is updated
     * @throws AuthenticationException if the user is not authenticated
     * @throws ValidationException if the item does not follow the validation given in the ItemDto
     * @throws ConflictException if AIS or storage does is not specified
     */
    DigitalStorageItem update(ItemDto item, String jwt) throws ConflictException, ValidationException, AuthenticationException;

    /**
     * Removes an {@link DigitalStorageItem} stored in the db.
     *
     * @param id an ID of a stored {@link DigitalStorageItem}
     * @param jwt a valid JWT of a user
     * @throws AuthenticationException if the user is not authenticated
     */
    void delete(Long id, String jwt) throws AuthenticationException;
}
