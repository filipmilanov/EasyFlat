package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeIngredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeIngredientDto(
    Long id,
    String name,
    String unit,
    UnitDto unitDto,
    double amount) {


    public static RecipeIngredientDto createWithCustomLogic(Long id, String name, String unit, UnitDto unitDto, double amount) {

        // Return the new instance of RecipeIngredientDto
        return new RecipeIngredientDto(id, name, unit, unitDto, amount);
    }


}

