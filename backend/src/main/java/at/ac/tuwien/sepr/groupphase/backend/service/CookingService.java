package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.CookbookDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeIngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cookbook;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeSuggestion;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;
import java.util.Optional;

public interface CookingService {

    /**
     * Get a list of recipe suggestions based on the provided store ID and type.
     *
     * @param type   The type of the recipe (e.g., breakfast, main dish ...).
     * @return A list of recipe suggestions.
     * @throws ValidationException If there is a validation error.
     */
    List<RecipeSuggestionDto> getRecipeSuggestion(String type, String jwt) throws ValidationException, ConflictException, AuthenticationException;

    /**
     * Get the details of a specific recipe based on the provided recipe ID.
     *
     * @param recipeId The ID of the recipe.
     * @return The details of the recipe.
     */
    RecipeDetailDto getRecipeDetails(Long recipeId);

    Cookbook createCookbook(CookbookDto cookbook, String jwt) throws ValidationException, ConflictException, AuthenticationException;

    List<Cookbook> findAllCookbooks(String jwt) throws AuthenticationException;

    /**
     * Get a list of recipes from the cookbook.
     *
     * @return A list of recipes.
     * @throws ValidationException If there is a validation error.
     */
    List<RecipeSuggestionDto> getCookbook(String jwt) throws ValidationException, AuthenticationException;

    /**
     * Create a new recipe in the cookbook.
     *
     * @param recipe The recipe details to be added to the cookbook.
     * @return The created recipe.
     * @throws ConflictException If there is a conflict with existing data.
     */
    RecipeSuggestion createCookbookRecipe(RecipeSuggestionDto recipe, String jwt) throws ConflictException, ValidationException, AuthenticationException;

    /**
     * Get a specific recipe from the cookbook based on its ID.
     *
     * @param id The ID of the recipe to retrieve.
     * @return An Optional containing the recipe, if found.
     */
    Optional<RecipeSuggestion> getCookbookRecipe(Long id, String jwt);

    /**
     * Update an existing recipe in the cookbook.
     *
     * @param recipe The updated recipe details.
     * @return The updated recipe.
     * @throws ConflictException If there is a conflict with existing data.
     */
    RecipeSuggestion updateCookbookRecipe(RecipeSuggestionDto recipe, String jwt) throws ConflictException, ValidationException, AuthenticationException;

    /**
     * Delete a recipe from the cookbook based on its ID.
     *
     * @param id The ID of the recipe to be deleted.
     * @return The deleted recipe.
     */
    RecipeSuggestion deleteCookbookRecipe(Long id, String jwt) throws AuthenticationException;

    /**
     * Get a list of missing ingredients for a specific recipe from the cookbook.
     *
     * @param id The ID of the recipe to check for missing ingredients.
     * @return The missing ingredients for the recipe.
     */
    RecipeSuggestionDto getMissingIngredients(Long id, String jwt) throws AuthenticationException, ValidationException, ConflictException;

    RecipeSuggestionDto cookRecipe(RecipeSuggestionDto recipeToCook, String jwt) throws ValidationException, ConflictException, AuthenticationException;
}
