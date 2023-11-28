package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlwaysInStockItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public abstract class ItemMapper {

    @Mapping(target = "storage", source = "digitalStorage")
    @Mapping(target = "ingredientList", expression = "java( ingredientList )")
    public abstract Item dtoToEntity(ItemDto itemDto,
                                     @Context List<Ingredient> ingredientList);

    @Mapping(target = "storage", source = "digitalStorage")
    @Mapping(target = "ingredientList", expression = "java( ingredientList )")
    public abstract AlwaysInStockItem dtoToAlwaysInStock(ItemDto itemDto,
                                                         @Context List<Ingredient> ingredientList);

    @Mapping(target = "digitalStorage", source = "storage")
    @Mapping(target = "ingredientsIdList", source = "ingredientList")
    @Mapping(target = "alwaysInStock", expression = "java( item.alwaysInStock() )")
    @Mapping(target = "minimumQuantity", expression = "java( item.getMinimumQuantity() )")
    public abstract ItemDto entityToDto(Item item);


    Long digitalStorageToId(DigitalStorage storage) {
        return storage.getStorId();
    }

    List<Long> ingredientListToIdList(List<Ingredient> ingredientList) {
        return ingredientList.stream().map(Ingredient::getIngrId).toList();
    }
}
