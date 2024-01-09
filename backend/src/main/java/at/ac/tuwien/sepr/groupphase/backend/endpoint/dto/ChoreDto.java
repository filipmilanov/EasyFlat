package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;

import java.time.LocalDate;
import java.util.Date;

public record ChoreDto(
    Long id,

    String choreName,

    String description,

    Date endDate,

    Integer points,

    UserDetailDto user
) {
}
