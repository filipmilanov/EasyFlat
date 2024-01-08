package at.ac.tuwien.sepr.groupphase.backend.scheduler;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.DebitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.ExpenseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.ExpenseDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.SplitBy;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
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
@ExtendWith(MockitoExtension.class)
public class ExpenseSchedulerTest {

    @Autowired
    private ExpenseScheduler expenseScheduler;

    @SpyBean
    private ExpenseService expenseService;

    @Autowired
    private TestDataGenerator testDataGenerator;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private AuthService authService;
    private ApplicationUser applicationUser;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();

        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);
    }

    private static Stream<Arguments> createRepeatingExpensesData() {
        return Stream.of(
            Arguments.of(
                List.of(
                    generateRepeatingExpense(1, LocalDateTime.now().minusDays(1)),
                    generateRepeatingExpense(1, LocalDateTime.now().minusDays(1)),
                    generateRepeatingExpense(1, LocalDateTime.now().minusDays(1))
                ),
                3
            ),
            Arguments.of(
                List.of(
                    generateRepeatingExpense(1, LocalDateTime.now().minusDays(1)),
                    generateRepeatingExpense(2, LocalDateTime.now().minusDays(1)),
                    generateRepeatingExpense(3, LocalDateTime.now().minusDays(1))
                ),
                1
            ),
            Arguments.of(
                List.of(
                    generateRepeatingExpense(2, LocalDateTime.now()),
                    generateRepeatingExpense(1, LocalDateTime.now()),
                    generateRepeatingExpense(2, LocalDateTime.now())
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

        // when
        expenseScheduler.createRepeatingExpense();

        // then
        verify(expenseService, times(toCreateCount + repeatingExpenseList.size())).create(any());

    }

    private static ExpenseDto generateRepeatingExpense(Integer periodInDays, LocalDateTime createdAt) {

        return ExpenseDtoBuilder.builder()
            .title("Test")
            .description("Test")
            .amountInCents(50.0)
            .createdAt(createdAt)
            .paidBy(generateListUser(6))
            .isRepeating(periodInDays != null)
            .periodInDays(periodInDays)
            .debitUsers(
                List.of(
                    DebitDtoBuilder.builder()
                        .user(generateListUser(6))
                        .splitBy(SplitBy.EQUAL)
                        .value(25.0)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(generateListUser(11))
                        .splitBy(SplitBy.EQUAL)
                        .value(25.0)
                        .build()
                )
            )
            .build();
    }

    private static UserListDto generateListUser(long id) {
        return UserListDtoBuilder.builder()
            .id(id)
            .build();
    }

}
