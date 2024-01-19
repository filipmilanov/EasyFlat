package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@RecordBuilder
public record ShoppingListDto(
    Long id,
    @NotBlank(message = "The list name cannot be empty")
    @NotEmpty(message = "The name cannot be empty")
    String name,
    Integer itemsCount

) {


}
