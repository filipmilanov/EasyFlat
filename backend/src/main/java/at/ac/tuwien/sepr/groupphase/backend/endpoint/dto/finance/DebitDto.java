package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.SplitBy;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Data transfer object for debits which is used for all splitBy strategies.
 */
@RecordBuilder
public record DebitDto(
    @NotNull(message = "The debit user must be defined")
    UserListDto user,

    @NotNull(message = "The split strategy ist not defined")
    SplitBy splitBy,
    @Positive(message = "The amount per user must be positive")
    Double value
) {
}
