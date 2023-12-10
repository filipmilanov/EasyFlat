package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.annotation.Nullable;

@RecordBuilder
public record UnitDto(
    String name,
    @Nullable Long conversionFactor,
    @Nullable UnitDto subUnit
) {
}
