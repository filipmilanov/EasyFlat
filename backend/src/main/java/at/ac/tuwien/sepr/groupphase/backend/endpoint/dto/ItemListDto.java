package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.NotEmpty;

@RecordBuilder
public record ItemListDto(
    @NotEmpty
    String generalName,
    @NotEmpty
    Long quantityCurrent,
    @NotEmpty
    Long quantityTotal,
    @NotEmpty
    Long storId,
    @NotEmpty
    String unit


) {
}
