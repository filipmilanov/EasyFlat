package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public abstract class IngredientMapper {

    public abstract List<Ingredient> dtoListToEntityList(List<IngredientDto> ingredientDtos);

    @Mapping(target = "ingrId", source = "ingredientId")
    @Mapping(target = "title", source = "name")
    public abstract Ingredient ingredientDtoToIngredient(IngredientDto ingredientDto);
}
