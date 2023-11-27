package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface ItemMapper {

    @Mapping(target = "storage", expression = "java( persistedDigitalStorage )")
    @Mapping(target = "ingredientList", expression = "java( ingredientList )")
    Item dtoToItem(ItemDto itemDto,
                   @Context DigitalStorage persistedDigitalStorage,
                   @Context List<Ingredient> ingredientList);

    ItemDto itemToDto(Item item);
}
