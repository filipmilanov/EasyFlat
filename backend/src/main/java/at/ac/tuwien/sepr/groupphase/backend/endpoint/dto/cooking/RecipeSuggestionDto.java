package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@RecordBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeSuggestionDto(
    Long id,
    @NotBlank(message = "The title cannot be blank")
    String title,
    @NotNull(message = "The servings cannot be empty")
    @Min(value = 1, message = "The servings must be positive")
    Integer servings,
    @NotNull(message = "The time in minutes cannot be empty")
    @Min(value = 1, message = "The time in minutes must be positive")
    Integer readyInMinutes,
    List<RecipeIngredientDto> extendedIngredients,
    @NotBlank(message = "The summary cannot be empty")
    String summary,
    List<RecipeIngredientDto> missedIngredients,
    List<String> dishTypes) {

    public RecipeSuggestionDto withId(Long newId) {
        return new RecipeSuggestionDto(newId, title, servings, readyInMinutes, extendedIngredients, summary, missedIngredients, dishTypes);
    }

    public RecipeSuggestionDto withExtendedIngredients(List<RecipeIngredientDto> newRecipeIngredientDtos) {
        return new RecipeSuggestionDto(id, title, servings, readyInMinutes, newRecipeIngredientDtos, summary, missedIngredients, dishTypes);
    }

    public RecipeSuggestionDto withSummaryAndWithoutMissingIngredients(String newSummary) {
        return new RecipeSuggestionDto(id, title, servings, readyInMinutes, extendedIngredients, newSummary, new ArrayList<>(), dishTypes);
    }
}
