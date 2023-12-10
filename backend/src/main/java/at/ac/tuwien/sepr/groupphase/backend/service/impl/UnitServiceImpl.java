package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UnitRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.UnitService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.UnitValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class UnitServiceImpl implements UnitService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final UnitRepository unitRepository;
    private final UnitValidator unitValidator;

    public UnitServiceImpl(UnitRepository unitRepository,
                           UnitValidator unitValidator) {
        this.unitRepository = unitRepository;
        this.unitValidator = unitValidator;
    }

    @Override
    public List<Unit> findAll() {
        LOGGER.info("findAll()");

        return unitRepository.findAll();
    }

    @Override
    public Long convertUnits(Unit from, Unit to, Long value) throws ValidationException, ConflictException {
        LOGGER.info("convertUnits({}, {}, {})", from, to, value);

        Unit persistedFrom = unitRepository.findByUnit(from.getUnit());
        Unit persistedTo = unitRepository.findByUnit(to.getUnit());

        unitValidator.validateUnit(from, to, persistedFrom, persistedTo);

        return value * persistedFrom.getConvertFactor();
    }
}
