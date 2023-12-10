package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;

import java.util.List;

public interface UnitService {

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
    Long convertUnits(Unit from, Unit to, Long value);
}
