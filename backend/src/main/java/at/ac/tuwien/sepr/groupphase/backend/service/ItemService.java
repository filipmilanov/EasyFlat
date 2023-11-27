package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;

import java.util.Optional;

/**
 * Service for working with Items.
 */
public interface ItemService {

    /**
     * Search for an item in the database with given ID.
     *
     * @param id a valid ID
     * @return if the id exists in the DB, an Optional of a persisted Item with given ID, an empty optional otherwise
     */
    Optional<Item> findById(Long id);

    /**
     * Validates and Creates a new {@link Item} in the db.
     *
     * @param item a storage without ID
     * @return an object of type {@link Item} which is persisted and has an ID
     */
    Item create(ItemDto item) throws ConflictException;

    /**
     * Validates and Updates a new {@link Item} in the db.
     *
     * @param item a storage with existing ID
     * @return an object of type {@link Item} which is updated
     */
    Item update(ItemDto item) throws ConflictException;

    /**
     * Removes an {@link Item} stored in the db.
     *
     * @param id an ID of a stored {@link Item}
     */
    void delete(Long id);
}
