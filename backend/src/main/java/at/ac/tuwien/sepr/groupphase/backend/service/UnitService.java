package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface UnitService {

    /**
     * Find unit by id.
     *
     * @param name the id of the unit
     * @return the unit
     */
    Unit findByName(String name);

    /**
     * Find all unit entries ordered by published at date (descending).
     *
     * @return list of al unit entries
     */
    List<Unit> findAll();

    /**
     * Calculates the value of units of the target unit from the given value of the source unit.
     *
     * @param from  source unit
     * @param to    target unit
     * @param value value of source unit
     * @return the converted value
     */
    Long convertUnits(Unit from, Unit to, Long value) throws ValidationException, ConflictException;


    /**
     * Creates a new unit.
     *
     * @param unit the unit to create
     * @return the created unit
     */
    Unit create(UnitDto unit) throws ValidationException, ConflictException;

}

