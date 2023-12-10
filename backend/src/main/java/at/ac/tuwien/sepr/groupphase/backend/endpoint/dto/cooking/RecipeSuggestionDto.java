package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeIngredientDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@RecordBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeSuggestionDto(
    Long id,
    @NotEmpty
    String title,
    Integer servings,
    Integer readyInMinutes,
    List<RecipeIngredientDto> extendedIngredients,
    String summary,
    List<RecipeIngredientDto> missedIngredients,
    List<String> dishTypes) {

    public RecipeSuggestionDto withId(Long newId) {
        return new RecipeSuggestionDto(newId, title, servings, readyInMinutes, extendedIngredients, summary, missedIngredients, dishTypes);
    }
}
