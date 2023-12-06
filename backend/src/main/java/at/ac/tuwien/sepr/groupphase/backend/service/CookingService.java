package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RecipeDto;

import java.util.List;

public interface CookingService {

    List<RecipeDto> getRecipeSuggestion();
}
