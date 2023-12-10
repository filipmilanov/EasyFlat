package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.service.UnitService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitServiceImpl implements UnitService {

    @Override
    public List<Unit> findAll() {
        return null;
    }

    @Override
    public Long convertUnits(Unit from, Unit to, Long value) {
        return null;
    }
}
