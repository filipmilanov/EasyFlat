package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@RecordBuilder
public record IngredientDto(
    Long ingredientId,
    @Size(max = 100, message = "One of the ingredients have name longer then 100 characters")
    @NotNull(message = "One of the ingredients have no name")
    String name
) {
}
