package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface CookingService {

    List<RecipeSuggestionDto> getRecipeSuggestion(Long storId) throws ValidationException;

    RecipeDetailDto getRecipeDetails(Long recipeId);

    List<RecipeSuggestionDto> getCookbook() throws ValidationException;
}
