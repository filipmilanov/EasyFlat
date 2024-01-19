package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.RepeatingExpenseType;
import at.ac.tuwien.sepr.groupphase.backend.entity.SplitBy;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data transfer object for expenses.
 * An Expense represents a payment made by a user for the shared flat.
 */
@RecordBuilder
public record ExpenseDto(
    Long id,
    @NotBlank(message = "Title cannot be empty")
    @Size(max = 255, message = "Title cannot be longer than 255 characters")
    String title,
    @Size(max = 255, message = "Description cannot be longer than 255 characters")
    String description,
    @NotNull(message = "Amount cannot be empty")
    @Min(value = 1, message = "Amount must be greater than 1")
    @Max(value = 1_000_000, message = "Amount must be less than 1.000.000")
    Double amountInCents,
    @NotNull(message = "A finance entry must have a creation date")
    LocalDateTime createdAt,
    @NotNull(message = "A finance entry must have a payer")
    UserListDto paidBy,
    @Valid
    List<DebitDto> debitUsers,
    List<ItemDto> items,
    Boolean isRepeating,
    @Min(value = 1, message = "The days until repeat must be greater then 1")
    Integer periodInDays,
    RepeatingExpenseType repeatingExpenseType
) {

    @AssertTrue(message = "Period of days or repeating type must be present if the finance entry is set to repeating")
    public boolean isPeriodPresentIfIsRepeating() {
        return isRepeating == null || !isRepeating || periodInDays != null || repeatingExpenseType != null;
    }

    @AssertTrue(message = "The sum of the users amount must be equal to the total amount")
    public boolean isSumOfDebitUsersAmountEqualToTotalAmount() {
        return debitUsers == null
            || List.of(SplitBy.PROPORTIONAL, SplitBy.PERCENTAGE).contains(debitUsers.get(0).splitBy())
            || Math.abs(debitUsers.stream().mapToDouble(DebitDto::value).sum() - amountInCents) < 5;
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

    public ExpenseDto withId(long newId) {
        return new ExpenseDto(
            newId,
            title,
            description,
            amountInCents,
            createdAt,
            paidBy,
            debitUsers,
            items,
            isRepeating,
            periodInDays,
            repeatingExpenseType
        );
    }
}

