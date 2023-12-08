package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;

import java.util.Optional;

public interface ShoppingListService {
    ShoppingItem create(ShoppingItemDto itemDto);

    Optional<ShoppingItem> getById(Long itemId);

    Optional<ShoppingItem> getItemsById(Long listId);
}
