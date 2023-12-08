package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.NotEmpty;

@RecordBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeDto(
    Long id,
    @NotEmpty(message = "The title cannot be empty")
    String title,
    String description,

    String image

) {

}
