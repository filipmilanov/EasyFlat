package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ItemMapper {

    @Mapping(target = "digitalStorage", source = "digitalStorage")
    @Mapping(target = " ingredientList", ignore = true)
    Item dtoToItem(ItemDto itemDto, DigitalStorage digitalStorage);

    ItemDto itemToDto(Item item);
}
