package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RecipeDto;
import at.ac.tuwien.sepr.groupphase.backend.service.CookingService;
import jakarta.annotation.security.PermitAll;
import org.springframework.web.bind.annotation.GetMapping;
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
    public List<RecipeDto> getRecipeSuggestion() {
        return cookingService.getRecipeSuggestion();
    }
}
