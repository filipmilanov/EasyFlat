package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeSuggestion;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface CookingService {


    /**
     * Get a list of recipe suggestions based on the provided store ID.
     *
     * @param storId The ID of the storage.
     * @return A list of recipe suggestions.
     * @throws ValidationException If there is a validation error.
     */
    List<RecipeSuggestionDto> getRecipeSuggestion(Long storId) throws ValidationException;

    /**
     * Get the details of a specific recipe based on the provided recipe ID.
     *
     * @param recipeId The ID of the recipe.
     * @return The details of the recipe.
     */
    RecipeDetailDto getRecipeDetails(Long recipeId);

    /**
     * Get a list of recipe suggestions from the cookbook.
     *
     * @return A list of recipe suggestions.
     * @throws ValidationException If there is a validation error.
     */
    List<RecipeSuggestionDto> getCookbook() throws ValidationException;

    RecipeSuggestion createCookbookRecipe(RecipeSuggestionDto recipe) throws ConflictException;
}
