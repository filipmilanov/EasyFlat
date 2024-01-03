package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record BalanceDebitDto(
    ApplicationUser debtor,
    ApplicationUser creditor,
    Double valueInCent
) {
}
