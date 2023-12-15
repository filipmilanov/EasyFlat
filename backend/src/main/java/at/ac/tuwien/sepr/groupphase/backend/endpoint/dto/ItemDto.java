package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.ItemStats;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

@RecordBuilder
public record ItemDto(
    Long itemId,
    @Pattern(regexp = "^\\d{13}$", message = "EAN number has exactly 13 numbers")
    String ean,
    @NotEmpty(message = "The general name cannot be empty")
    @Size(max = 100, message = "The general name can't exceed 100 characters")
    String generalName,
    @NotEmpty(message = "The product name cannot be empty")
    @Size(max = 100, message = "The product name can't exceed 100 characters")
    String productName,
    @NotEmpty(message = "The brand name cannot be empty")
    @Size(max = 100, message = "The brand name can't exceed 100 characters")
    String brand,
    @NotNull(message = "The actual quantity cannot be empty")
    @Min(value = 0, message = "The actual quantity must be positive")
    @Max(value = 100000000, message = "The maximum value for current quantity has been exceeded")
    Double quantityCurrent,
    @NotNull(message = "The total quantity cannot be empty")
    @Min(value = 0, message = "The total quantity must be positive")
    @Max(value = 100000000, message = "The maximum value for total quantity has been exceeded")
    Double quantityTotal,
    @NotNull(message = "The unit cannot be null")
    UnitDto unit,
    @FutureOrPresent(message = "You cannot store products which are over the expire date")
    LocalDate expireDate,
    @Size(max = 100, message = "The description can't exceed 100 characters")
    String description,
    @Min(value = 0, message = "The price must be positive")
    @Max(value = 100000000, message = "The maximum value for the price has been exceeded")
    Long priceInCent,
    Boolean alwaysInStock,
    @Min(value = 0, message = "The minimum quantity must be positive")
    Long minimumQuantity,
    @Size(max = 100, message = "The store name can't exceed 100 characters")
    String boughtAt,
    @NotNull(message = "An item needs to be linked to a storage")
    DigitalStorageDto digitalStorage,
    List<IngredientDto> ingredients,
    List<ItemStats> itemStats
) {
    @AssertTrue(message = "The current quantity cannot be larger then the total")
    private boolean isQuantityCurrentLessThenTotal() {
        return this.quantityCurrent == null
            || this.quantityTotal == null
            || this.quantityCurrent <= this.quantityTotal;
    }

    @AssertTrue(message = "The minimum quantity cannot be empty")
    private boolean isMinimumQuantityNotEmpty() {
        return this.alwaysInStock == null || !this.alwaysInStock || this.minimumQuantity != null;
    }

    public ItemDto withId(long newId) {
        return new ItemDto(
            newId,
            ean,
            generalName,
            productName,
            brand,
            quantityCurrent,
            quantityTotal,
            unit,
            expireDate,
            description,
            priceInCent,
            alwaysInStock,
            minimumQuantity,
            boughtAt,
            digitalStorage,
            ingredients,
            itemStats
        );
    }

    public ItemDto withAlwaysInStock(Boolean alwaysInStock) {
        return new ItemDto(
            itemId,
            ean,
            generalName,
            productName,
            brand,
            quantityCurrent,
            quantityTotal,
            unit,
            expireDate,
            description,
            priceInCent,
            alwaysInStock,
            minimumQuantity,
            boughtAt,
            digitalStorage,
            ingredients,
            itemStats
        );
    }

    @Override
    public String toString() {
        return "ItemDto{"
            + "itemId=" + itemId
            + ", ean='" + ean + '\''
            + ", generalName='" + generalName + '\''
            + ", productName='" + productName + '\''
            + ", brand='" + brand + '\''
            + ", quantityCurrent=" + quantityCurrent
            + ", quantityTotal=" + quantityTotal
            + ", unit='" + unit + '\''
            + ", expireDate=" + expireDate
            + ", description='" + description + '\''
            + ", priceInCent=" + priceInCent
            + ", alwaysInStock=" + alwaysInStock
            + ", minimumQuantity=" + minimumQuantity
            + ", boughtAt='" + boughtAt + '\''
            + ", digitalStorage=" + digitalStorage
            + ", ingredients=" + ingredients
            + '}';
    }

    public ItemDto withUpdatedQuantity(Double updatedQuantity) {
        return new ItemDto(
            itemId,
            ean,
            generalName,
            productName,
            brand,
            updatedQuantity,
            quantityTotal,
            unit,
            expireDate,
            description,
            priceInCent,
            alwaysInStock,
            minimumQuantity,
            boughtAt,
            digitalStorage,
            ingredients,
            itemStats
        );
    }
}