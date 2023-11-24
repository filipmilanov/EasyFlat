package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;

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
     * Validates and Creates a new {@link DigitalStorage} in the db.
     *
     * @param storage a storage without ID
     * @return an object of type {@link DigitalStorage} which is persisted and has an ID
     */
    DigitalStorage create(DigitalStorage storage);

    /**
     * Validates and Updates a new {@link DigitalStorage} in the db.
     *
     * @param storage a storage with existing ID
     * @return an object of type {@link DigitalStorage} which is updated
     */
    DigitalStorage update(DigitalStorage storage);

    /**
     * Removes an {@link DigitalStorage} stored in the db.
     *
     * @param id an ID of a stored {@link DigitalStorage}
     */
    void remove(Long id);
}
