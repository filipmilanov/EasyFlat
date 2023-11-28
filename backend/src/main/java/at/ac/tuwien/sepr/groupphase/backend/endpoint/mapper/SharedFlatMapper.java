package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import org.mapstruct.Mapper;


@Mapper
public abstract class SharedFlatMapper {
    public abstract WgDetailDto entityToWgDetailDto(SharedFlat sharedFlat);
}
