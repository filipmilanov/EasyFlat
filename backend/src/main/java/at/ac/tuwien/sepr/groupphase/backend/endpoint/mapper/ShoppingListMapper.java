package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlwaysInStockItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper
public abstract class ShoppingListMapper {

    @Mapping(target = "id", source = "shopListId")
    @Mapping(target = "listName", source = "name")
    public abstract ShoppingListDto entityToDto(ShoppingList shoppingList);

    @Mapping(target = "shopListId", source = "id")
    @Mapping(target = "name", source = "listName")
    public abstract ShoppingList dtoToEntity(ShoppingListDto shoppingListDto);

    public abstract List<ShoppingListDto> entityListToDtoList(List<ShoppingList> shoppingList);

    public Item shoppingItemDtoToItem(ShoppingItemDto shoppingItem, List<Ingredient> ingredients) {
        if (shoppingItem == null) {
            return null;
        }
        Item item = new Item();
        item.setEan(shoppingItem.ean());
        item.setGeneralName(shoppingItem.generalName());
        item.setProductName(shoppingItem.productName());
        item.setBrand(shoppingItem.brand());
        item.setQuantityCurrent(shoppingItem.quantityCurrent());
        item.setQuantityTotal(shoppingItem.quantityTotal());
        item.setUnit(shoppingItem.unit());
        item.setExpireDate(null);
        item.setDescription(shoppingItem.description());
        item.setPriceInCent(shoppingItem.priceInCent());
        item.setBoughtAt(shoppingItem.boughtAt());
        item.setStorage(new DigitalStorage(1L, "Main"));
        item.setIngredientList(ingredients);
        return item;
    }

    public AlwaysInStockItem shoppingItemDtoToAis(ShoppingItemDto shoppingItem, List<Ingredient> ingredients) {
        if (shoppingItem == null) {
            return null;
        }
        AlwaysInStockItem item = new AlwaysInStockItem();
        item.setEan(shoppingItem.ean());
        item.setGeneralName(shoppingItem.generalName());
        item.setProductName(shoppingItem.productName());
        item.setBrand(shoppingItem.brand());
        item.setQuantityCurrent(shoppingItem.quantityCurrent());
        item.setQuantityTotal(shoppingItem.quantityTotal());
        item.setUnit(shoppingItem.unit());
        item.setExpireDate(null);
        item.setDescription(shoppingItem.description());
        item.setPriceInCent(shoppingItem.priceInCent());
        item.setBoughtAt(shoppingItem.boughtAt());
        item.setStorage(new DigitalStorage(1L, "Main"));
        item.setIngredientList(ingredients);
        item.setMinimumQuantity(shoppingItem.minimumQuantity());
        return item;
    }

}
