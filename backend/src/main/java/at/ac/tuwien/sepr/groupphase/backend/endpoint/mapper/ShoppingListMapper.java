package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlwaysInStockDigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = {UnitMapper.class, IngredientMapper.class, DigitalStorageMapper.class})
public abstract class ShoppingListMapper {

    @Mapping(target = "id", source = "shopListId")
    public abstract ShoppingListDto entityToDto(ShoppingList shoppingList);

    @Mapping(target = "shopListId", source = "id")
    public abstract ShoppingList dtoToEntity(ShoppingListDto shoppingListDto);

    public abstract List<ShoppingListDto> entityListToDtoList(List<ShoppingList> shoppingList);


    @Mapping(target = "ingredientList", source = "ingredients")
    public abstract DigitalStorageItem shoppingItemDtoToItem(ShoppingItemDto shoppingItemDto,
                                                             List<Ingredient> ingredients,
                                                             DigitalStorage digitalStorage);

    @Mapping(target = "ingredientList", source = "ingredients")
    public abstract AlwaysInStockDigitalStorageItem shoppingItemDtoToAis(ShoppingItemDto shoppingItem,
                                                                         List<Ingredient> ingredients,
                                                                         DigitalStorage digitalStorage);
}
