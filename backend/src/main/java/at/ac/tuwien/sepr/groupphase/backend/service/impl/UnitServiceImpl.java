package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UnitMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
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
    private final UnitMapper unitMapper;

    public UnitServiceImpl(UnitRepository unitRepository,
                           UnitValidator unitValidator,
                           UnitMapper unitMapper) {
        this.unitRepository = unitRepository;
        this.unitValidator = unitValidator;
        this.unitMapper = unitMapper;
    }

    @Override
    public Unit findByName(String name) {
        LOGGER.info("findByName({})", name);

        return unitRepository.findByName(name).orElseThrow(() -> new NotFoundException("Unit not found"));
    }


    @Override
    public List<Unit> findAll() {
        LOGGER.info("findAll()");

        return unitRepository.findAll();
    }

    @Override
    public Double convertUnits(Unit from, Unit to, Double value) throws ValidationException, ConflictException {
        LOGGER.info("convertUnits({}, {}, {})", from, to, value);


        if (to.equals(from)) {
            return value;
        }

        if (this.getMinUnit(from).equals(to)) {
            return value * from.getConvertFactor();
        } else {
            return value / to.getConvertFactor();
        }

    }

    @Override
    public Unit create(UnitDto unit) throws ValidationException, ConflictException {
        LOGGER.info("create({})", unit);

        unitValidator.validateForCreate(unit);

        Unit unitEntity = unitMapper.unitDtoToEntity(unit);
        return unitRepository.save(unitEntity);
    }

    private Unit getMinUnit(Unit unit) {
        if (!unit.getSubUnit().isEmpty()) {
            for (Unit subUnit : unit.getSubUnit()) {
                return subUnit;
            }
        }
        return unit;
    }
}
