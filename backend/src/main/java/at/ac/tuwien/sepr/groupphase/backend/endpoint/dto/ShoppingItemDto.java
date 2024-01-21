package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

@RecordBuilder
public record ShoppingItemDto(
    Long itemId,
    @Pattern(regexp = "^\\d{13}$")
    String ean,
    @NotBlank(message = "The product category cannot be blank")
    @Size(max = 30, message = "The product category cannot have more than 30 characters")
    String generalName,
    @NotBlank(message = "The product name cannot be blank")
    @Size(max = 40, message = "The product name cannot have more than 40 characters")
    String productName,
    @Size(max = 30, message = "The brand name cannot have more than 30 characters")
    String brand,
    @Min(value = 0, message = "Quantity must be positive")
    @Max(value = 5000, message = "The current quantity cannot be greater than 5000")
    Double quantityCurrent,
    @Min(value = 0)
    @Max(value = 5000)
    Double quantityTotal,
    @NotNull(message = "The unit cannot be null")
    UnitDto unit,
    String description,
    @Min(value = 0)
    Long priceInCent,
    Boolean alwaysInStock,
    @Min(value = 0)
    @Max(value = 5000)
    Double minimumQuantity,
    String boughtAt,
    List<IngredientDto> ingredients,
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
            ingredients,
            labels,
            shoppingList
        );
    }
}
