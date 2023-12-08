package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record ItemLabelDto(
    Long labelId,
    String labelValue,
    String labelColour
) {
}
