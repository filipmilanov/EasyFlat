package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.RecipeMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeSuggestion;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.CookingService;
import jakarta.annotation.security.PermitAll;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public List<RecipeSuggestionDto> getRecipeSuggestion() throws ValidationException {
        return cookingService.getRecipeSuggestion(1L);
    }

    @PermitAll
    @GetMapping("/cookbook")
    public List<RecipeSuggestionDto> getCookbook() throws ValidationException {
        return cookingService.getCookbook();
    }

    @PermitAll
    @PostMapping("/cookbook")
    public RecipeSuggestionDto createCookbookRecipe(@RequestBody RecipeSuggestionDto recipe) throws ConflictException {
        return recipeMapper.entityToRecipeSuggestionDto(cookingService.createCookbookRecipe(recipe));
    }

    @PermitAll
    @GetMapping("/detail/{id}")
    public RecipeDetailDto getRecipeDetail(@PathVariable Long id) {
        return cookingService.getRecipeDetails(id);
    }
}
