package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.ItemLabel;
import jakarta.validation.constraints.NotEmpty;

public record ShoppingListDto(
    Long id,

    @NotEmpty(message = "The title cannot be empty") String name
) {


}
