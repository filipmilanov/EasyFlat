package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeIngredientDto(
    Long id,
    String name,
    String unit,
    double amount) {
}
