package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.RepeatingExpenseTyp;
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
    @NotEmpty(message = "Title cannot be empty")
    String title,
    String description,
    @NotNull(message = "Amount cannot be empty")
    @Min(value = 1, message = "Amount must be greater than 1")
    Double amountInCents,
    @NotNull(message = "A finance entry must have a creation date")
    LocalDateTime createdAt,
    @NotNull(message = "A finance entry must have a payer")
    UserListDto paidBy,
    List<DebitDto> debitUsers,
    List<ItemDto> items,
    Boolean isRepeating,
    @Min(value = 1, message = "The days until repeat must be greater then 1")
    Integer periodInDays,
    RepeatingExpenseTyp repeatingExpenseTyp
) {

    @AssertTrue(message = "Period of days or Repeating Typ must be present if the finance entry is set to repeating")
    public boolean isPeriodPresentIfIsRepeating() {
        return isRepeating == null || !isRepeating || periodInDays != null || repeatingExpenseTyp != null;
    }

    @AssertTrue(message = "The sum of the users amount must be equal to the total amount")
    public boolean isSumOfDebitUsersAmountEqualToTotalAmount() {
        return debitUsers == null
            || List.of(SplitBy.PROPORTIONAL, SplitBy.PERCENTAGE).contains(debitUsers.get(0).splitBy())
            || Math.abs(debitUsers.stream().mapToDouble(DebitDto::value).sum() - amountInCents) < 0.1;
    }


    @AssertTrue(message = "The sum of the users percent must be equal to 100")
    public boolean isSumOfPercent100() {
        return debitUsers == null
            || List.of(SplitBy.UNEQUAL, SplitBy.EQUAL, SplitBy.PROPORTIONAL).contains(debitUsers.get(0).splitBy())
            || debitUsers.stream().mapToDouble(DebitDto::value).sum() == 100;
    }

    @AssertTrue(message = "The split strategy must be equal in all debit users")
    public boolean isSplitStrategyEqualInAllDebitUsers() {
        return debitUsers == null
            || debitUsers.stream().map(DebitDto::splitBy).distinct().count() == 1;
    }
}

