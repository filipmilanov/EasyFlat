package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Negative;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Date;

@RecordBuilder
public record ChoreDto(
    Long id,

    @NotNull(message = "The name cannot be empty")
    @Size(max = 200, message = "The name is too long")
    String name,

    @Size(max = 200, message = "The description is too long")
    String description,

    LocalDate endDate,

    @Min(value = 0, message = "Points should be at least 0")
    @Max(value = 100, message = "Points can be at most 100")
    Integer points,

    UserDetailDto user
) {
    public ChoreDto trimmedName(String name) {
        return new ChoreDto(this.id, name, this.description, this.endDate, this.points, this.user);
    }

    public ChoreDto trimmed(String name, String description) {
        return new ChoreDto(this.id, name, description, this.endDate, this.points, this.user);
    }
}
