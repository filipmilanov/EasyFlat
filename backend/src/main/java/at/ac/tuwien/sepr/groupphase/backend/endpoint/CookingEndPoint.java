package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.RecipeMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeSuggestion;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.CookingService;
import jakarta.annotation.security.PermitAll;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/cooking")
public class CookingEndPoint {
    private final CookingService cookingService;
    private final RecipeMapper recipeMapper;

    public CookingEndPoint(CookingService cookingService, RecipeMapper recipeMapper) {
        this.cookingService = cookingService;
        this.recipeMapper = recipeMapper;
    }

    @PermitAll
    @GetMapping
    public List<RecipeSuggestionDto> getRecipeSuggestion(String type, @RequestHeader("Authorization") String jwt) throws ValidationException, ConflictException, AuthenticationException {
        return cookingService.getRecipeSuggestion(type, jwt);
    }

    @PermitAll
    @GetMapping("/cookbook")
    public List<RecipeSuggestionDto> getCookbook(@RequestHeader("Authorization") String jwt) throws ValidationException, AuthenticationException {
        return cookingService.getCookbook(jwt);
    }

    @PermitAll
    @PostMapping("/cookbook")
    public RecipeSuggestionDto createCookbookRecipe(@RequestBody RecipeSuggestionDto recipe, @RequestHeader("Authorization") String jwt)
        throws ConflictException, ValidationException, AuthenticationException {
        return recipeMapper.entityToRecipeSuggestionDto(cookingService.createCookbookRecipe(recipe, jwt));
    }

    @PermitAll
    @GetMapping("/cookbook/{id}")
    public Optional<RecipeSuggestionDto> getCookbookRecipe(@PathVariable Long id, @RequestHeader("Authorization") String jwt) {
        Optional<RecipeSuggestion> recipe = cookingService.getCookbookRecipe(id, jwt);
        return recipe.flatMap(currentRecipe -> Optional.ofNullable(recipeMapper.entityToRecipeSuggestionDto(currentRecipe)));
    }

    @PermitAll
    @PutMapping("/cookbook/{id}")
    public RecipeSuggestionDto updateCookbookRecipe(@PathVariable Long id, @RequestBody RecipeSuggestionDto recipe, @RequestHeader("Authorization") String jwt)
        throws ConflictException, ValidationException, AuthenticationException {
        return recipeMapper.entityToRecipeSuggestionDto(cookingService.updateCookbookRecipe(recipe.withId(id), jwt));
    }

    @PermitAll
    @DeleteMapping("/cookbook/{id}")
    public RecipeSuggestionDto deleteCookbookRecipe(@PathVariable Long id, @RequestHeader("Authorization") String jwt) throws AuthenticationException {
        return recipeMapper.entityToRecipeSuggestionDto(cookingService.deleteCookbookRecipe(id, jwt));
    }

    @PermitAll
    @GetMapping("/cookbook/missing/{id}")
    public RecipeSuggestionDto getMissingIngredients(@PathVariable Long id, @RequestHeader("Authorization") String jwt)
        throws AuthenticationException, ValidationException, ConflictException {
        return cookingService.getMissingIngredients(id, jwt);
    }

    @PermitAll
    @GetMapping("/detail/{id}")
    public RecipeDetailDto getRecipeDetail(@PathVariable Long id, @RequestHeader("Authorization") String jwt) {
        return cookingService.getRecipeDetails(id);
    }

    @PermitAll
    @PutMapping("/cook")
    public RecipeSuggestionDto cookRecipe(@RequestBody RecipeSuggestionDto recipeToCook, @RequestHeader("Authorization") String jwt) throws ValidationException, ConflictException, AuthenticationException {
        return cookingService.cookRecipe(recipeToCook, jwt);
    }

}
