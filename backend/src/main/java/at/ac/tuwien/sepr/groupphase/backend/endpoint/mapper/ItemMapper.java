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
public abstract class ItemMapper {

    @Mapping(target = "storage", expression = "java( persistedDigitalStorage )")
    @Mapping(target = "ingredientList", expression = "java( ingredientList )")
    public abstract Item dtoToItem(ItemDto itemDto,
                   @Context DigitalStorage persistedDigitalStorage,
                   @Context List<Ingredient> ingredientList);

    @Mapping(target = "storageId", source = "storage")
    @Mapping(target = "ingredientsIdList", source = "ingredientList")
    public abstract ItemDto itemToDto(Item item);

    Long digitalStorageToId(DigitalStorage storage) {
        return storage.getStorId();
    }

    List<Long> ingredientListToIdList(List<Ingredient> ingredientList) {
        return ingredientList.stream().map(Ingredient::getIngrId).toList();
    }
}
