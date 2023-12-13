package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeIngredientDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@RecordBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeSuggestionDto(
    Long id,
    @NotEmpty(message = "The title cannot be empty")
    String title,
    @NotNull(message = "The servings cannot be empty")
    @Min(value = 1, message = "The servings must be positive")
    Integer servings,
    @NotNull(message = "The time in minutes cannot be empty")
    @Min(value = 1, message = "The time in minutes must be positive")
    Integer readyInMinutes,
    List<RecipeIngredientDto> extendedIngredients,
    @NotEmpty(message = "The summary cannot be empty")
    String summary,
    List<RecipeIngredientDto> missedIngredients,
    List<String> dishTypes) {

    public RecipeSuggestionDto withId(Long newId) {
        return new RecipeSuggestionDto(newId, title, servings, readyInMinutes, extendedIngredients, summary, missedIngredients, dishTypes);
    }
}
