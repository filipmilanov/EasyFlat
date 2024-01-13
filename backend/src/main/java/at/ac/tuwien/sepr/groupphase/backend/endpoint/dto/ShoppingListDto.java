package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

public record ShoppingListDto(
    Long id,
    String name,
    List<ShoppingItemDto> items

) {


}
