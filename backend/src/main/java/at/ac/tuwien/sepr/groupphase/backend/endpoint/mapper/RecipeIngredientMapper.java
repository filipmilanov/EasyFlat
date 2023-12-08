package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RecipeIngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeIngredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeSuggestion;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public abstract class RecipeIngredientMapper {


    public abstract List<RecipeIngredient> dtoListToEntityList(List<RecipeIngredientDto> recipeIngredientDtos);

    public abstract RecipeIngredient dtoToEntity(RecipeIngredientDto recipeIngredientDto);

    public abstract RecipeIngredientDto entityToDto(RecipeIngredient recipeIngredient);

}
