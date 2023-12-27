package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class ChoreMapper {

    @Mapping(target = "name", source = "choreName")
    public abstract Chore choreDtoToEntity(ChoreDto choreDto);

    @Mapping(target = "choreName", source = "name")
    public abstract ChoreDto entityToChoreDto(Chore chore);
}
