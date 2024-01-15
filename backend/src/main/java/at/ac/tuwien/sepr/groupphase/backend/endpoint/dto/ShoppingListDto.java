package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ShoppingListDto(
    Long id,
    @NotEmpty(message = "The name cannot be empty")String name,
    List<ShoppingItemDto> items

) {


}
