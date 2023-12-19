package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpenFoodFactsItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlwaysInStockItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemLabel;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemStats;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = {IngredientMapper.class, DigitalStorageMapper.class, UnitMapper.class})
public abstract class ItemMapper {

    @Mapping(target = "storage", source = "digitalStorage")
    @Mapping(target = "ingredientList", expression = "java( ingredientList )")
    public abstract Item dtoToEntity(ItemDto itemDto,
                                     @Context List<Ingredient> ingredientList,
                                     @Context List<ItemStats> itemStats);

    @Mapping(target = "storage", source = "digitalStorage")
    @Mapping(target = "ingredientList", expression = "java( ingredientList )")
    public abstract AlwaysInStockItem dtoToAlwaysInStock(ItemDto itemDto,
                                                         @Context List<Ingredient> ingredientList,
                                                         @Context List<ItemStats> itemStats);

    @Mapping(target = "digitalStorage", source = "storage")
    @Mapping(target = "ingredients", source = "ingredientList")
    @Mapping(target = "alwaysInStock", expression = "java( item.alwaysInStock() )")
    @Mapping(target = "minimumQuantity", expression = "java( item.getMinimumQuantity() )")
    public abstract ItemDto entityToDto(Item item);


    Long digitalStorageToId(DigitalStorage storage) {
        return storage.getStorId();
    }

    List<Long> ingredientListToIdList(List<Ingredient> ingredientList) {
        return ingredientList.stream().map(Ingredient::getIngrId).toList();
    }

    @Mapping(target = "alwaysIsStock", expression = "java( itemDto.alwaysInStock() )")
    @Mapping(target = "labels", expression = "java( labels )")
    @Mapping(target = "shoppingList", expression = "java( shoppingList )")
    public abstract ShoppingItem dtoToShopping(ShoppingItemDto itemDto,
                                               @Context List<ItemLabel> labels,
                                                @Context ShoppingList shoppingList);

    @Mapping(target = "labels", source = "labels")
    @Mapping(target = "alwaysInStock", expression = "java( item.alwaysInStock() )")
    @Mapping(target = "shoppingList", expression = "java( shoppingList )")
    public abstract ShoppingItemDto entityToShopping(ShoppingItem item,
                                                     @Context ShoppingListDto shoppingList);

    public abstract List<ShoppingItemDto> shoppingItemListToShoppingDto(List<ShoppingItem> items);

    @Mapping(target = "alwaysIsStock", expression = "java( itemDto.alwaysInStock() )")
    @Mapping(target = "ingredientList", expression = "java( ingredients )")
    @Mapping(target = "shoppingList", expression = "java( shoppingList )")
    public abstract ShoppingItem itemDtoToShoppingItem(ItemDto itemDto,
                                              @Context List<Ingredient> ingredients,
                                              @Context ShoppingList shoppingList);

    public abstract List<ItemDto> entityListToItemDtoList(List<Item> items);

    @Mapping(target = "ean", source = "eanCode")
    public abstract ItemDto openFoodFactItemDtoToItemDto(OpenFoodFactsItemDto openFoodFactsItemDto);

}
