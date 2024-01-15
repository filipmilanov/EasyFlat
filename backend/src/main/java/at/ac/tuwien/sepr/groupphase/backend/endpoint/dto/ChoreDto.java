package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.Date;

public record ChoreDto(
    Long id,

    @NotEmpty(message = "The name cannot be empty")String name,

    String description,

    Date endDate,

    Integer points,

    UserDetailDto user
) {
}
