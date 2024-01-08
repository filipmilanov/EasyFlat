package at.ac.tuwien.sepr.groupphase.backend.scheduler;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.ExpenseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.ExpenseDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class ExpenseSchedulerTest {

    @Autowired
    private ExpenseScheduler expenseScheduler;

    @MockBean
    private ExpenseService expenseService;

    @Autowired
    private TestDataGenerator testDataGenerator;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();
    }

    private static Stream<Arguments> createRepeatingExpensesData() {
        return Stream.of(
            Arguments.of(
                List.of(ExpenseDtoBuilder.builder()
                        .title("Repeating 1")
                        .description("Repeating 1")
                        .amountInCents(1000.0)
                        .isRepeating(true)
                        .periodInDays(1)
                        .createdAt(LocalDateTime.now().minusDays(1))
                        .build(),
                    ExpenseDtoBuilder.builder()
                        .title("Repeating 2")
                        .description("Repeating 2")
                        .amountInCents(1000.0)
                        .isRepeating(true)
                        .periodInDays(1)
                        .createdAt(LocalDateTime.now().minusDays(1))
                        .build(),
                    ExpenseDtoBuilder.builder()
                        .title("Repeating 2")
                        .description("Repeating 2")
                        .amountInCents(1000.0)
                        .isRepeating(true)
                        .periodInDays(1)
                        .createdAt(LocalDateTime.now().minusDays(1))
                        .build()
                ),
                3
            ),
            Arguments.of(
                List.of(ExpenseDtoBuilder.builder()
                        .title("Repeating 1")
                        .description("Repeating 1")
                        .amountInCents(1000.0)
                        .isRepeating(true)
                        .periodInDays(1)
                        .createdAt(LocalDateTime.now().minusDays(1))
                        .build(),
                    ExpenseDtoBuilder.builder()
                        .title("Repeating 2")
                        .description("Repeating 2")
                        .amountInCents(1000.0)
                        .isRepeating(true)
                        .periodInDays(2)
                        .createdAt(LocalDateTime.now().minusDays(1))
                        .build(),
                    ExpenseDtoBuilder.builder()
                        .title("Repeating 2")
                        .description("Repeating 2")
                        .amountInCents(1000.0)
                        .isRepeating(true)
                        .periodInDays(3)
                        .createdAt(LocalDateTime.now().minusDays(1))
                        .build()
                ),
                1
            ),
            Arguments.of(
                List.of(ExpenseDtoBuilder.builder()
                        .title("Repeating 1")
                        .description("Repeating 1")
                        .amountInCents(1000.0)
                        .isRepeating(true)
                        .periodInDays(2)
                        .createdAt(LocalDateTime.now())
                        .build(),
                    ExpenseDtoBuilder.builder()
                        .title("Repeating 2")
                        .description("Repeating 2")
                        .amountInCents(1000.0)
                        .isRepeating(true)
                        .periodInDays(1)
                        .createdAt(LocalDateTime.now())
                        .build(),
                    ExpenseDtoBuilder.builder()
                        .title("Repeating 2")
                        .description("Repeating 2")
                        .amountInCents(1000.0)
                        .isRepeating(true)
                        .periodInDays(2)
                        .createdAt(LocalDateTime.now())
                        .build()
                ),
                0
            ),
            Arguments.of(List.of(), 0)
        );
    }

    @ParameterizedTest
    @DisplayName("Create repeating expenses")
    @MethodSource("createRepeatingExpensesData")
    void createRepeatingExpenses(List<ExpenseDto> repeatingExpenseList, int toCreateCount) throws ValidationException, ConflictException, AuthenticationException {
        // given
        repeatingExpenseList.forEach(expenseDto ->
            {
                try {
                    expenseService.create(expenseDto);
                } catch (ValidationException | ConflictException | AuthenticationException e) {
                    throw new RuntimeException(e);
                }
            }
        );

        when(expenseService.create(any())).thenReturn(new Expense());

        // when
        expenseScheduler.createRepeatingExpense();

        // then
        verify(expenseService, times(toCreateCount + repeatingExpenseList.size())).create(any());

    }


}
