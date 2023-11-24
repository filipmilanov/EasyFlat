package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DigitalStorageMapper {


    @Mapping(target = "itemList", ignore = true)
    DigitalStorage dtoToEntity(DigitalStorageDto digitalStorageDto);
}
