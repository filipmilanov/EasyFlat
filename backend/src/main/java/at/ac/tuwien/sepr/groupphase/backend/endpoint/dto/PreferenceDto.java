package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;

import java.util.List;

public record PreferenceDto(
    Long id,

    ChoreDto first,

    ChoreDto second,

    ChoreDto third,

    ChoreDto fourth
) {

}
