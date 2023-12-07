package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RecipeDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RecipeSuggestionDto;

import java.util.List;

public interface CookingService {

    List<RecipeSuggestionDto> getRecipeSuggestion();
}
