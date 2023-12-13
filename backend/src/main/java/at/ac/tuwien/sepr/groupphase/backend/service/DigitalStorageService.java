package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemOrderType;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;
import java.util.Optional;

/**
 * Service for working with Storages.
 */
public interface DigitalStorageService {

    /**
     * Search for a storage in the database with given ID.
     *
     * @param id a valid ID
     * @return if the id exists in the DB, an Optional of a persisted DigitalStorage with given ID, an empty optional otherwise
     */
    Optional<DigitalStorage> findById(Long id);

    /**
     * Search for all Storages stored in the database which matches with the given search criteria.
     *
     * @param digitalStorageSearchDto search criteria
     * @param jwt                     a valid jwt
     * @return a List of all persisted Storages
     */
    List<DigitalStorage> findAll(DigitalStorageSearchDto digitalStorageSearchDto, String jwt) throws AuthenticationException;

    /**
     * Search for all Items of a DigitalStorage stored in the database.
     *
     * @param id an ID of a DigitalStorage
     * @return if the id exists a List of all correlated items
     */
    List<Item> findAllItemsOfStorage(Long id);

    /**
     * Search for all Items of a DigitalStorage stored in the database ordered by defined orderType.
     *
     * @param id        an ID of a DigitalStorage
     * @param orderType defines how to order
     * @return if the id exists a List of all correlated items ordered by orderType
     */
    List<Item> findAllItemsOfStorageOrdered(Long id, ItemOrderType orderType);

    /**
     * Search for all Items of a DigitalStorage stored in the database filtered by search parameters.
     *
     * @param itemSearchDto search parameters
     * @param jwt           a valid jwt
     * @return a List of filtered items
     */
    List<ItemListDto> searchItems(ItemSearchDto itemSearchDto, String jwt) throws ValidationException, AuthenticationException;

    /**
     * Validates and Creates a new {@link DigitalStorage} in the db.
     *
     * @param storageDto a storage without ID
     * @param jwt        a valid jwt
     * @return an object of type {@link DigitalStorage} which is persisted and has an ID
     */
    DigitalStorage create(DigitalStorageDto storageDto, String jwt) throws ConflictException, ValidationException, AuthenticationException;

    /**
     * Validates and Updates a new {@link DigitalStorage} in the db.
     *
     * @param storageDto a storage with existing ID
     * @return an object of type {@link DigitalStorage} which is updated
     */
    DigitalStorage update(DigitalStorageDto storageDto);

    /**
     * Removes an {@link DigitalStorage} stored in the db.
     *
     * @param id an ID of a stored {@link DigitalStorage}
     */
    void remove(Long id);

    /**
     * Updates currentQuantity of the item with specified digitalStorage and itemId in db.
     *
     * @param storageId existing ID of a storage
     * @param itemId    existing ID of an item
     * @param quantity  the new quantity of the specified item
     * @return an updated object of type {@link Item}
     */
    Item updateItemQuantity(long storageId, long itemId, long quantity);

    /**
     * Retrieves a list of items with a specific general name
     * and associated with the user identified by the provided JWT.
     *
     * @param name The general name to filter items by.
     * @param jwt  A valid JWT token for user authentication.
     * @return A list of items with the specified general name.
     * @throws AuthenticationException If authentication fails or the user does not exist.
     */
    List<Item> getItemWithGeneralName(String name, String jwt) throws AuthenticationException;
}
