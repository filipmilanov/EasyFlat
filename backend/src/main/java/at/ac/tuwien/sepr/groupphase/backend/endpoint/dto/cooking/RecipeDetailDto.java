package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@RecordBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeDetailDto(
    Long id,
    @NotEmpty
    String title,
    Integer servings,
    Integer readyInMinutes,
    List<RecipeIngredientDto> extendedIngredients,
    String summary,
    CookingSteps steps

) {


}
