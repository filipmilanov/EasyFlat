package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
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

    public abstract ShoppingListDto entityToDto(ShoppingList shoppingList);

    public abstract ShoppingList dtoToEntity(ShoppingListDto shoppingListDto);

    public abstract List<ShoppingListDto> entityListToDtoList(List<ShoppingList> shoppingList);

    public Item shoppingItemDtoToItem(ShoppingItemDto shoppingItem, List<Ingredient> ingredients) {
        if (shoppingItem == null) {
            return null;
        }
        Item item = new Item();
        item.setEan(shoppingItem.ean());
        item.setGeneralName(shoppingItem.generalName());
        item.setProductName(shoppingItem.generalName());
        item.setBrand(shoppingItem.brand());
        item.setQuantityCurrent(shoppingItem.quantityCurrent());
        item.setQuantityTotal(shoppingItem.quantityTotal());
        item.setUnit(shoppingItem.unit());
        item.setExpireDate(null);
        item.setDescription(shoppingItem.description());
        item.setPriceInCent(shoppingItem.priceInCent());
        item.setBoughtAt(shoppingItem.boughtAt());
        item.setStorage(new DigitalStorage(1L));
        item.setIngredientList(ingredients);
        return item;
    }

}
