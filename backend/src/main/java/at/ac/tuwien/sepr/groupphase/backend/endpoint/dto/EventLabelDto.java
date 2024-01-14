package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record EventLabelDto(
    Long id,
    String labelName,
    String labelColour
) {

}
