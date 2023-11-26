package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemOrderType;
import jakarta.xml.bind.ValidationException;

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
     * Validates and Creates a new {@link DigitalStorage} in the db.
     *
     * @param storageDto a storage without ID
     * @return an object of type {@link DigitalStorage} which is persisted and has an ID
     */
    DigitalStorage create(DigitalStorageDto storageDto) throws ValidationException;

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
}
