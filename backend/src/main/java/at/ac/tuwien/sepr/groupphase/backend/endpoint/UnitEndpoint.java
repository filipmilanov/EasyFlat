package at.ac.tuwien.sepr.groupphase.backend.endpoint;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitConvertDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UnitMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.UnitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping("/api/v1/unit")
public class UnitEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final UnitService unitService;
    private final UnitMapper unitMapper;

    public UnitEndpoint(UnitService unitService, UnitMapper unitMapper) {
        this.unitService = unitService;
        this.unitMapper = unitMapper;
    }

    @GetMapping("/all")
    public List<UnitDto> findAll() {
        LOGGER.info("findAll()");

        return unitMapper.entityListToUnitDtoList(
            unitService.findAll()
        );
    }

    @GetMapping("/convert")
    public Long convertUnits(UnitConvertDto unitConvertDto) {
        LOGGER.info("convertUnits()");

        return unitService.convertUnits(
            unitMapper.unitDtoToEntity(unitConvertDto.from()),
            unitMapper.unitDtoToEntity(unitConvertDto.to()),
            unitConvertDto.value()
        );
    }


}
