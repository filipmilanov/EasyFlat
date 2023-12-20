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
     * @return if the id exists in the DB, an Optional of a persisted DigitalStorageItem with given ID, an empty optional otherwise
     */
    DigitalStorageItem findById(Long id, String jwt) throws AuthenticationException;

    /**
     * Search for an item in the database where one field is matching
     * .
     *
     * @param itemFieldSearchDto fields to search for
     * @return a list of items matching the search criteria
     */
    List<DigitalStorageItem> findByFields(ItemFieldSearchDto itemFieldSearchDto);

    /**
     * Validates and Creates a new {@link DigitalStorageItem} in the db.
     *
     * @param item a storage without ID
     * @param jwt  a valid JWT of a user
     * @return an object of type {@link DigitalStorageItem} which is persisted and has an ID
     */
    DigitalStorageItem create(ItemDto item, String jwt) throws ConflictException, ValidationException, AuthenticationException;

    /**
     * Validates and Updates a new {@link DigitalStorageItem} in the db.
     *
     * @param item a storage with existing ID
     * @param jwt a valid JWT of a user
     * @return an object of type {@link DigitalStorageItem} which is updated
     */
    DigitalStorageItem update(ItemDto item, String jwt) throws ConflictException, ValidationException, AuthenticationException;

    /**
     * Removes an {@link DigitalStorageItem} stored in the db.
     *
     * @param id an ID of a stored {@link DigitalStorageItem}
     * @param jwt a valid JWT of a user
     */
    void delete(Long id, String jwt) throws AuthenticationException;
}
