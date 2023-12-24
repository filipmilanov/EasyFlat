package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class EventMapper {


    public abstract Event dtoToEntity(EventDto eventDto);

    public abstract EventDto entityToDto(Event event);
}
