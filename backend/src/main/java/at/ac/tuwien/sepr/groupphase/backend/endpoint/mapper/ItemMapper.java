package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ItemMapper {

    @Mapping(target = "storage", ignore = true)
    @Mapping(target = " ingredientList", ignore = true)
    Item itemDtoToItem(ItemDto itemDto);
}
