package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemLabel;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemStats;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.List;

public record ShoppingItemDto(
    Long itemId,

    @Pattern(regexp = "^\\d{13}$", message = "EAN number has exactly 13 numbers")
    String ean,
    String generalName,
    String productName,
    String brand,
    @Min(value = 0, message = "The actual quantity must be positive")
    Long quantityCurrent,
    @Min(value = 0, message = "The total quantity must be positive")
    Long quantityTotal,
    @NotNull(message = "The unit cannot be null")
    UnitDto unit,
    String description,
    @Min(value = 0, message = "The price must be positive")
    Long priceInCent,
    Boolean alwaysInStock,
    @Min(value = 0, message = "The minimum quantity must be positive")
    Long minimumQuantity,
    String boughtAt,
    DigitalStorageDto digitalStorage,
    List<IngredientDto> ingredients,
    List<ItemStats> itemStats,
    List<ItemLabelDto> labels,
    ShoppingListDto shoppingList
) {

    public ShoppingItemDto withId(long newId) {
        return new ShoppingItemDto(
            newId,
            ean,
            generalName,
            productName,
            brand,
            quantityCurrent,
            quantityTotal,
            unit,
            description,
            priceInCent,
            alwaysInStock,
            minimumQuantity,
            boughtAt,
            digitalStorage,
            ingredients,
            itemStats,
            labels,
            shoppingList
        );
    }
}
