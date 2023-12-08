package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RecipeIngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemStats;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeIngredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeSuggestion;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public abstract class RecipeMapper {

    @Mapping(target = "extendedIngredients", expression = "java( ingredients )")
    public abstract RecipeSuggestion dtoToEntity(RecipeSuggestionDto recipeSuggestionDto,
                                                 @Context List<RecipeIngredient> ingredients);

    public abstract RecipeSuggestionDto entityToRecipeSuggestionDto(RecipeSuggestion recipeSuggestion);
}
