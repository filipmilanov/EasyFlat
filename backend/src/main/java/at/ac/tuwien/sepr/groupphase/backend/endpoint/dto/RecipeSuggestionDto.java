package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

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
    int servings,
    int readyInMinutes,
    List<RecipeIngredientDto> extendedIngredients,
    String summary) {

}
