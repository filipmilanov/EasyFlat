package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@RecordBuilder
public record ShoppingListDto(
    Long id,
    @NotBlank(message = "The name of the shopping list cannot be empty")
    @NotEmpty(message = "The name of the shopping list cannot be empty")
    @Size(max = 40, message = "The name of the shopping list cannot have more than 40 characters")
    String name,
    Integer itemsCount

) {


}
