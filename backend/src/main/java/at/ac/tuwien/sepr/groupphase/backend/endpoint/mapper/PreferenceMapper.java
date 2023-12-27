package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import org.mapstruct.Mapper;

@Mapper
public abstract class PreferenceMapper {

    public abstract Preference preferenceDtoToEntity(PreferenceDto preferenceDto);

    public abstract PreferenceDto entityToPreferenceDto(Preference preference);
}
