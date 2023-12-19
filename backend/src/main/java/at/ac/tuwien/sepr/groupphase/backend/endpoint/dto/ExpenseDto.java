package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@RecordBuilder
public record ExpenseDto(
    Long id,
    @NotEmpty(message = "Title cannot be empty") String title,
    String description,
    @NotNull(message = "Amount cannot be empty")
    Long amountInCents,
    @NotNull(message = "CreatedAt cannot be null")
    LocalDateTime createdAt,
    @NotNull(message = "Payer cannot be null")
    UserListDto paidBy,
    List<DebitDto> debitUsers,
    @NotNull(message = "The shared flat cannot be empty") WgDetailDto sharedFlat,
    List<ItemDto> items,
    Boolean isRepeating,
    LocalDateTime interval
) {

    @AssertTrue(message = "Interval must be present if isRepeating is true")
    public boolean isPeriodPresentIfIsRepeating() {
        return isRepeating == null || !isRepeating || interval != null;
    }

    ExpenseDto withSharedFlat(WgDetailDto sharedFlat) {
        return new ExpenseDto(
            id,
            title,
            description,
            amountInCents,
            createdAt,
            paidBy,
            debitUsers,
            sharedFlat,
            items,
            isRepeating,
            interval
        );
    }
}

