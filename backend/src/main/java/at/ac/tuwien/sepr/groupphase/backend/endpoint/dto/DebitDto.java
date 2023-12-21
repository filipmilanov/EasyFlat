package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.SplitBy;
import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * Data transfer object for debits which is used for all splitBy strategies.
 */
@RecordBuilder
public record DebitDto(
    UserListDto user,
    SplitBy splitBy,
    Double value
) {
}
