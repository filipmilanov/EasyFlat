package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ItemMapper {

    @Mapping(target = "storage", expression = "java( persistedDigitalStorage )")
    @Mapping(target = "ingredientList", ignore = true)
    Item dtoToItem(ItemDto itemDto, @Context DigitalStorage persistedDigitalStorage);

    ItemDto itemToDto(Item item);
}
