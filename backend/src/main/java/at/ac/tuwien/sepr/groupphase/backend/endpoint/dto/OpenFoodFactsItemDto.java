package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.List;

@RecordBuilder
public record OpenFoodFactsItemDto(
    String eanCode,
    String generalName,
    String productName,
    String brand,
    Long quantityTotal,
    String unit,
    String description,
    String boughtAt,
    List<Ingredient> ingredients
) {
}
