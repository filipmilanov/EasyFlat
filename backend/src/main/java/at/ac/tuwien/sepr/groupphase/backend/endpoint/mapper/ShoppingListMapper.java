package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlwaysInStockItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(uses = {UnitMapper.class, IngredientMapper.class, DigitalStorageMapper.class})
public abstract class ShoppingListMapper {

    @Mapping(target = "id", source = "shopListId")
    @Mapping(target = "listName", source = "name")
    public abstract ShoppingListDto entityToDto(ShoppingList shoppingList);

    @Mapping(target = "shopListId", source = "id")
    @Mapping(target = "name", source = "listName")
    public abstract ShoppingList dtoToEntity(ShoppingListDto shoppingListDto);

    public abstract List<ShoppingListDto> entityListToDtoList(List<ShoppingList> shoppingList);


    @Mapping(target = "ingredientList", source = "ingredients")
    @Mapping(target = "storage", source = "digitalStorage")
    public abstract Item shoppingItemDtoToItem(ShoppingItemDto shoppingItemDto,
                                               List<Ingredient> ingredients,
                                               DigitalStorage digitalStorage);

    @Mapping(target = "ingredientList", source = "ingredients")
    @Mapping(target = "storage", source = "digitalStorage")
    public abstract AlwaysInStockItem shoppingItemDtoToAis(ShoppingItemDto shoppingItem,
                                                           List<Ingredient> ingredients,
                                                           DigitalStorage digitalStorage);
}
