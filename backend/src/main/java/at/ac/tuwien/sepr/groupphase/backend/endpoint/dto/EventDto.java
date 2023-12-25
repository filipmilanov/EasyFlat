package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;

@RecordBuilder
public record EventDto(
    Long id,
    @NotEmpty
    String title,
    String description,
    @NotEmpty
    @FutureOrPresent
    LocalDate date
) {


}
