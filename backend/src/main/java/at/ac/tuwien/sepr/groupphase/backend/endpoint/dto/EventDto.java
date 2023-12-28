package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.EventLabel;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

@RecordBuilder
public record EventDto(
    Long id,
    @NotEmpty(message = "Title must not be empty")
    String title,
    String description,

    @FutureOrPresent(message = "The date must be in the present or in the future")
    LocalDate date,

    WgDetailDto sharedFlat,
    List<EventLabelDto> labels
) {
}
