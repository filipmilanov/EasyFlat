package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeIngredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

@RecordBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeIngredientDto(
    Long id,
    @NotBlank(message = "The ingredient name cannot be empty")
    String name,
    String unit,
    UnitDto unitEnum,
    @Min(value = 1, message = "The ingredient amount must be positive")
    double amount,
    boolean matched,
    boolean autoMatched,
    String realName,
    ItemDto matchedItem) {


    public static RecipeIngredientDto createWithCustomLogic(Long id, String name, String unit, UnitDto unitEnum, double amount, boolean matched, boolean autoMatched, String realName, ItemDto matchedItem) {

        // Return the new instance of RecipeIngredientDto
        return new RecipeIngredientDto(id, name, unit, unitEnum, amount, matched, autoMatched, realName, matchedItem);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecipeIngredientDto that = (RecipeIngredientDto) o;
        return Double.compare(amount, that.amount) == 0 && matched == that.matched && autoMatched == that.autoMatched
            && Objects.equals(name, that.name) && Objects.equals(unit, that.unit) && Objects.equals(unitEnum, that.unitEnum)
            && Objects.equals(realName, that.realName) && Objects.equals(matchedItem, that.matchedItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, unit, unitEnum, amount, matched, autoMatched, realName, matchedItem);
    }
}

