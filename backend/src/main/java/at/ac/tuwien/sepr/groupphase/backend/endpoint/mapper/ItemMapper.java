package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpenFoodFactsItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlwaysInStockDigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
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

    @Mapping(target = "itemCache.ean", source = "ean")
    @Mapping(target = "itemCache.generalName", source = "generalName")
    @Mapping(target = "itemCache.productName", source = "productName")
    @Mapping(target = "itemCache.brand", source = "brand")
    @Mapping(target = "itemCache.quantityTotal", source = "quantityTotal")
    @Mapping(target = "itemCache.unit", source = "unit")
    @Mapping(target = "itemCache.description", source = "description")
    @Mapping(target = "itemCache.ingredientList", expression = "java( ingredientList )")
    public abstract DigitalStorageItem dtoToEntity(ItemDto itemDto,
                                                   @Context List<Ingredient> ingredientList,
                                                   @Context List<ItemStats> itemStats);

    @Mapping(target = "itemCache.ean", source = "ean")
    @Mapping(target = "itemCache.generalName", source = "generalName")
    @Mapping(target = "itemCache.productName", source = "productName")
    @Mapping(target = "itemCache.brand", source = "brand")
    @Mapping(target = "itemCache.quantityTotal", source = "quantityTotal")
    @Mapping(target = "itemCache.unit", source = "unit")
    @Mapping(target = "itemCache.description", source = "description")
    @Mapping(target = "ingredientList", expression = "java( ingredientList )")
    public abstract AlwaysInStockDigitalStorageItem dtoToAlwaysInStock(ItemDto itemDto,
                                                                       @Context List<Ingredient> ingredientList,
                                                                       @Context List<ItemStats> itemStats);


    @Mapping(target = "ean", source = "itemCache.ean")
    @Mapping(target = "generalName", source = "itemCache.generalName")
    @Mapping(target = "productName", source = "itemCache.productName")
    @Mapping(target = "brand", source = "itemCache.brand")
    @Mapping(target = "quantityTotal", source = "itemCache.quantityTotal")
    @Mapping(target = "unit", source = "itemCache.unit")
    @Mapping(target = "description", source = "itemCache.description")
    @Mapping(target = "ingredients", source = "itemCache.ingredientList")
    @Mapping(target = "alwaysInStock", expression = "java( digitalStorageItem.alwaysInStock() )")
    @Mapping(target = "minimumQuantity", expression = "java( digitalStorageItem.getMinimumQuantity() )")
    public abstract ItemDto entityToDto(DigitalStorageItem digitalStorageItem);

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


    @Mapping(target = "itemCache.ean", source = "ean")
    @Mapping(target = "itemCache.generalName", source = "generalName")
    @Mapping(target = "itemCache.productName", source = "productName")
    @Mapping(target = "itemCache.brand", source = "brand")
    @Mapping(target = "itemCache.quantityTotal", source = "quantityTotal")
    @Mapping(target = "itemCache.unit", source = "unit")
    @Mapping(target = "itemCache.description", source = "description")
    @Mapping(target = "alwaysIsStock", expression = "java( itemDto.alwaysInStock() )")
    @Mapping(target = "itemCache.ingredientList", expression = "java( ingredients )")
    @Mapping(target = "shoppingList", expression = "java( shoppingList )")
    public abstract ShoppingItem itemDtoToShoppingItem(ItemDto itemDto,
                                              @Context List<Ingredient> ingredients,
                                              @Context ShoppingList shoppingList);

    public abstract List<ItemDto> entityListToItemDtoList(List<DigitalStorageItem> digitalStorageItems);

    @Mapping(target = "ean", source = "eanCode")
    public abstract ItemDto openFoodFactItemDtoToItemDto(OpenFoodFactsItemDto openFoodFactsItemDto);

}
