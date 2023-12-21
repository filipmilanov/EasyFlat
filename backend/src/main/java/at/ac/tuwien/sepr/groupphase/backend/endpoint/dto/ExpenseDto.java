package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.SplitBy;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data transfer object for expenses.
 * An Expense represents a payment made by a user for the shared flat.
 */
@RecordBuilder
public record ExpenseDto(
    Long id,
    @NotEmpty(message = "Title cannot be empty") String title,
    String description,
    @NotNull(message = "Amount cannot be empty")
    @Min(value = 1, message = "Amount must be greater than 1")
    Double amountInCents,
    @NotNull(message = "A finance entry must have a creation date")
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

    @AssertTrue(message = "The sum of the users amount must be equal to the total amount")
    public boolean isSumOfDebitUsersAmountEqualToTotalAmount() {
        return debitUsers == null
            || List.of(SplitBy.PROPORTIONAL, SplitBy.PERCENTAGE).contains(debitUsers.get(0).splitBy())
            || debitUsers.stream().mapToDouble(DebitDto::value).sum() == amountInCents;
    }


    @AssertTrue(message = "The sum of the users percent must be equal to 100")
    public boolean isSumOfPercent100() {
        return debitUsers == null
            || List.of(SplitBy.UNEQUAL, SplitBy.EQUAL, SplitBy.PROPORTIONAL).contains(debitUsers.get(0).splitBy())
            || debitUsers.stream().mapToDouble(DebitDto::value).sum() == 100;
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

