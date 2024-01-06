package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.ItemStats;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.List;

@RecordBuilder
public record ItemDto(
    Long itemId,
    @Pattern(regexp = "^\\d{13}$", message = "EAN number has exactly 13 numbers")
    String ean,
    @NotBlank(message = "The product category cannot be empty")
    String generalName,
    @NotBlank(message = "The product name cannot be empty")
    String productName,
    String brand,
    @NotNull(message = "The current quantity cannot be empty")
    @Min(value = 0, message = "The current quantity must be at least 0")
    @Max(value = 10000, message = "The current quantity cannot be greater than 10000")
    Double quantityCurrent,
    @NotNull(message = "The total quantity cannot be empty")
    @Min(value = 0, message = "The total quantity must be at least 0")
    @Max(value = 10000, message = "The total quantity cannot be greater than 10000")
    Double quantityTotal,
    @NotNull(message = "The unit cannot be empty")
    UnitDto unit,
    @FutureOrPresent(message = "You cannot store products which have already expired")
    LocalDate expireDate,
    String description,
    @Min(value = 0, message = "The price must be at least €0.00")
    Long priceInCent,
    Boolean alwaysInStock,
    @Min(value = 0, message = "The minimum quantity must be at least 0")
    @Max(value = 5000, message = "The minimum quantity cannot be greater than 5000")
    Long minimumQuantity,
    String boughtAt,
    @NotNull(message = "An item needs to be linked to a storage")
    DigitalStorageDto digitalStorage,
    List<IngredientDto> ingredients,
    List<ItemStats> itemStats,

    List<AlternativeNameDto> alternativeNames


) {
    //@AssertTrue(message = "The current quantity cannot be larger then the total")
    private boolean isQuantityCurrentLessThenTotal() {
        return this.quantityCurrent == null
            || this.quantityTotal == null
            || this.quantityCurrent <= this.quantityTotal;
    }

    @AssertTrue(message = "The minimum quantity cannot be empty")
    private boolean isMinimumQuantityNotEmpty() {
        return this.alwaysInStock == null || !this.alwaysInStock || this.minimumQuantity != null;
    }

    /**
     * This method converts the current quantity to a string and then uses regex to
     * check if the number does not exceed the maximum amount of decimal places.
     *
     * @return true - if it is valid; false - if it is not valid
     */
    //@AssertTrue(message = "The current quantity cannot have more than 2 decimal places")
    private boolean isQuantityCurrentValidDecimalPlaces() {
        int maximumDecimalPlaces = 2;

        String valueString = this.quantityCurrent.toString();

        String regex = "^\\d+(\\.\\d{1," + maximumDecimalPlaces + "})?$";
        // fully qualified name necessary due to conflict with Jakarta Pattern
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);

        return pattern.matcher(valueString).matches();
    }

    /**
     * This method converts the total quantity to a string and then uses regex tos
     * check if the number does not exceed the maximum amount of decimal places.
     *
     * @return true - if it is valid; false - if it is not valid
     */
    @AssertTrue(message = "The total quantity cannot have more than 2 decimal places")
    private boolean isQuantityTotalValidDecimalPlaces() {
        int maximumDecimalPlaces = 2;

        String valueString = this.quantityTotal.toString();

        String regex = "^\\d+(\\.\\d{1," + maximumDecimalPlaces + "})?$";
        // fully qualified name necessary due to conflict with Jakarta Pattern
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);

        return pattern.matcher(valueString).matches();
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
            itemStats,
            alternativeNames
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
            itemStats,
            alternativeNames
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
            itemStats,
            alternativeNames
        );
    }
}