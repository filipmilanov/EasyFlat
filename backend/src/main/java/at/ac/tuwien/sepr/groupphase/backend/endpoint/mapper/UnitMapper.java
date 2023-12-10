package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public abstract class UnitMapper {

    public abstract UnitDto entityToUnitDto(Unit unit);

    public abstract Unit unitDtoToEntity(UnitDto unitDto);

    public abstract List<UnitDto> entityListToUnitDtoList(List<Unit> unitDtoList);

    public abstract List<Unit> unitDtoListToEntityList(List<UnitDto> unitDtoList);
}