package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemFieldSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
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
     * @return an Item with the given ID
     * @throws AuthorizationException if the user is not authenticated
     */
    Item findById(Long id, String jwt) throws AuthorizationException;

    /**
     * Search for an item in the database where one field is matching
     * .
     *
     * @param itemFieldSearchDto fields to search for
     * @return a list of items matching the search criteria
     */
    List<Item> findByFields(ItemFieldSearchDto itemFieldSearchDto);


    /**
     * Validates and Creates a new {@link Item} in the db.
     *
     * @param item a storage without ID
     * @param jwt  a valid JWT of a user
     * @return an object of type {@link Item} which is persisted and has an ID
     */
    Item create(ItemDto item, String jwt) throws ConflictException, ValidationException, AuthorizationException;

    /**
     * Validates and Updates a new {@link Item} in the db.
     *
     * @param item a storage with existing ID
     * @param jwt a valid JWT of a user
     * @return an object of type {@link Item} which is updated
     */
    Item update(ItemDto item, String jwt) throws ConflictException, ValidationException, AuthorizationException;

    /**
     * Removes an {@link Item} stored in the db.
     *
     * @param id an ID of a stored {@link Item}
     * @param jwt a valid JWT of a user
     */
    void delete(Long id, String jwt) throws AuthorizationException;
}
