package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.Date;

public record ChoreDto(
    Long id,

    String choreName,

    String description,

    Date endDate,

    Integer points
) {
}
