package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@RecordBuilder
public record ExpenseDto(
    Long id,
    @NotEmpty(message = "Title cannot be empty") String title,
    @NotNull(message = "Description cannot be null") String description,
    @NotNull(message = "Amount cannot be empty")
    Long amountInCents,
    @NotNull(message = "Payer cannot be null")
    UserDetailDto paidBy,
    List<DebitDto> debitUsers,
    @NotNull(message = "The shared flat cannot be empty") WgDetailDto sharedFlat
) {

}

