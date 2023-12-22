package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
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
     * @param jwt  A valid JWT token for user authentication.
     * @return a List of all persisted Storages
     * @throws AuthorizationException if the user is not authenticated
     */
    List<DigitalStorage> findAll(DigitalStorageSearchDto digitalStorageSearchDto, String jwt) throws AuthorizationException;

    /**
     * Search for all Items of a DigitalStorage stored in the database filtered by search parameters.
     *
     * @param itemSearchDto search parameters
     * @param jwt  A valid JWT token for user authentication.
     * @return a List of filtered items
     * @throws AuthorizationException if the user is not authenticated
     * @throws ValidationException // TODO add reason
     * @throws ConflictException // TODO add reason
     */
    // TODO: Should this be in ItemService?
    List<ItemListDto> searchItems(ItemSearchDto itemSearchDto, String jwt) throws AuthorizationException, ValidationException, ConflictException;

    /**
     * Validates and Creates a new {@link DigitalStorage} in the db.
     *
     * @param storageDto a storage without ID
     * @param jwt  A valid JWT token for user authentication.
     * @return an object of type {@link DigitalStorage} which is persisted and has an ID
     * @throws AuthorizationException if the user is not authenticated
     * @throws ValidationException if the given storageDto contains invalid values
     * @throws ConflictException if the given storageDto has an ID
     */
    DigitalStorage create(DigitalStorageDto storageDto, String jwt) throws AuthorizationException, ValidationException, ConflictException;

    /**
     * Validates and Updates a new {@link DigitalStorage} in the db.
     *
     * @param storageDto a storage with existing ID
     * @return an object of type {@link DigitalStorage} which is updated
     */
    DigitalStorage update(DigitalStorageDto storageDto);

    /**
     * Gets an item from digital storage and adds it to the main shopping list.
     *
     * @param itemDto existing ID of a storage
     * @param jwt  A valid JWT token for user authentication.
     * @return the added item of type {@link ShoppingItem}
     * @throws AuthorizationException If authentication fails or the user does not exist.
     */
    ShoppingItem addItemToShopping(ItemDto itemDto, String jwt) throws AuthorizationException;
}
