package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.EventLabel;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RecordBuilder
public record EventDto(
    Long id,
    @NotEmpty(message = "Title must not be empty")
    String title,
    String description,
    @NotNull(message = "The start time must be given")
    LocalTime startTime,
    @NotNull(message = "The end time must be given")
    LocalTime endTime,
    @FutureOrPresent(message = "The date must be in the present or in the future") @NotNull(message = "The date must be given")
    LocalDate date,
    WgDetailDto sharedFlat,
    List<EventLabelDto> labels
) {

    @AssertTrue(message = "Start time must be before end time")
    public boolean isStartTimeBeforeEndTime() {
        return startTime == null || endTime == null || startTime.isBefore(endTime);
    }
}
