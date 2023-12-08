package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class ShoppingListMapper {

    public abstract ShoppingListDto entityToDto(ShoppingList shoppingList);

    public abstract ShoppingList dtoToEntity(ShoppingListDto shoppingListDto);
}
