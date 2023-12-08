package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.CookingService;
import jakarta.annotation.security.PermitAll;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/cooking")
public class CookingEndPoint {

    private CookingService cookingService;

    public CookingEndPoint(CookingService cookingService) {
        this.cookingService = cookingService;
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
    @GetMapping("/detail/{id}")
    public RecipeDetailDto getRecipeDetail(@PathVariable Long id) {
        return cookingService.getRecipeDetails(id);
    }
}
