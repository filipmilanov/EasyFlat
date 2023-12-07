package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RecipeDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface CookingService {

    List<RecipeSuggestionDto> getRecipeSuggestion(Long storId) throws ValidationException;

    List<RecipeSuggestionDto> getCookbook() throws ValidationException;
}
