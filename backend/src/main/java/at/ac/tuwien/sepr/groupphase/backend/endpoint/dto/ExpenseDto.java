package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.SplitBy;

import java.util.List;

public record ExpenseDto(
    Long id,
    String title,
    String description,
    SplitBy splitBy,
    Long amountInCents,
    UserDetailDto paidBy,
    List<DebitDto> debitUsers,
    WgDetailDto sharedFlat
) {

}

