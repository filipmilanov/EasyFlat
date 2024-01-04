package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;

import java.util.List;

public record PreferenceDto(
    Long id,

    String first,

    String second,

    String third,

    String fourth
) {

}
