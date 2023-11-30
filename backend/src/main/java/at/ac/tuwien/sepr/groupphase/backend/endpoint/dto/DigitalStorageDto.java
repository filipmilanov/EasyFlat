package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;


import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.NotEmpty;

@RecordBuilder
public record DigitalStorageDto(
    Long storId,
    @NotEmpty(message = "The title cannot be empty") String title
) {
}
