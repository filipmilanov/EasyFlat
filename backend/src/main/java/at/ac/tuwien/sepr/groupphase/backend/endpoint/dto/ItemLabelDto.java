package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@RecordBuilder
public record ItemLabelDto(
    Long labelId,
    @NotNull(message = "The label text cannot be empty")
    @NotBlank(message = "The label text cannot be blank")
    @Size(max = 30, message = "The label text cannot have more than 30 characters")
    String labelValue,
    String labelColour
) {
}
