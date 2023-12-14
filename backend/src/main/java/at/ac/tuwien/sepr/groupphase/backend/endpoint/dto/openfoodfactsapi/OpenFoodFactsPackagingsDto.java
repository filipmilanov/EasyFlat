package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenFoodFactsPackagingsDto(
    @JsonProperty("quantity_per_unit_unit")
    String unit
) {
}
