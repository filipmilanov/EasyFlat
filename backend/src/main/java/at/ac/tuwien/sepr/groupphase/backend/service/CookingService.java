package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeIngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeSuggestion;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;
import java.util.Optional;

public interface CookingService {

    /**
     * Get a list of recipe suggestions based on the provided store ID and type.
     *
     * @param storId The ID of the storage.
     * @param type   The type of the recipe (e.g., breakfast, main dish ...).
     * @return A list of recipe suggestions.
     * @throws ValidationException If there is a validation error.
     */
    List<RecipeSuggestionDto> getRecipeSuggestion(Long storId, String type) throws ValidationException, ConflictException;

    /**
     * Get the details of a specific recipe based on the provided recipe ID.
     *
     * @param recipeId The ID of the recipe.
     * @return The details of the recipe.
     */
    RecipeDetailDto getRecipeDetails(Long recipeId);

    /**
     * Get a list of recipes from the cookbook.
     *
     * @return A list of recipes.
     * @throws ValidationException If there is a validation error.
     */
    List<RecipeSuggestionDto> getCookbook() throws ValidationException;

    /**
     * Create a new recipe in the cookbook.
     *
     * @param recipe The recipe details to be added to the cookbook.
     * @return The created recipe.
     * @throws ConflictException If there is a conflict with existing data.
     */
    RecipeSuggestion createCookbookRecipe(RecipeSuggestionDto recipe) throws ConflictException;

    /**
     * Get a specific recipe from the cookbook based on its ID.
     *
     * @param id The ID of the recipe to retrieve.
     * @return An Optional containing the recipe, if found.
     */
    Optional<RecipeSuggestion> getCookbookRecipe(Long id);

    /**
     * Update an existing recipe in the cookbook.
     *
     * @param recipe The updated recipe details.
     * @return The updated recipe.
     * @throws ConflictException If there is a conflict with existing data.
     */
    RecipeSuggestion updateCookbookRecipe(RecipeSuggestionDto recipe) throws ConflictException;

    /**
     * Delete a recipe from the cookbook based on its ID.
     *
     * @param id The ID of the recipe to be deleted.
     * @return The deleted recipe.
     */
    RecipeSuggestion deleteCookbookRecipe(Long id);

    /**
     * Get a list of missing ingredients for a specific recipe from the cookbook.
     *
     * @param id The ID of the recipe to check for missing ingredients.
     * @return The missing ingredients for the recipe.
     */
    RecipeSuggestionDto getMissingIngredients(Long id);

    RecipeSuggestionDto cookRecipe(RecipeSuggestionDto recipeToCook) throws ValidationException, ConflictException;
}
