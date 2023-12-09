package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.List;

@RecordBuilder
public record ExpenseDto(
    Long id,
    String title,
    String description,
    Long amountInCents,
    UserDetailDto paidBy,
    List<DebitDto> debitUsers,
    WgDetailDto sharedFlat
) {

}

