package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.BalanceDebitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.DebitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.DebitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.ExpenseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.ExpenseDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.ExpenseSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.UserValuePairDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.RepeatingExpenseType;
import at.ac.tuwien.sepr.groupphase.backend.entity.SplitBy;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.SharedFlatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class ExpenseServiceTest {

    @Autowired
    private ExpenseService service;

    @Autowired
    private SharedFlatRepository sharedFlatRepository;

    @Autowired
    private TestDataGenerator testDataGenerator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @MockBean
    private AuthService authService;

    private ApplicationUser applicationUser;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();

        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);
    }

    @Test
    void givenValidIdWhenFindByIdThenExpenseWithCorrectIdIsReturned() throws AuthenticationException {
        // given
        long id = 5L;

        // when
        Expense actual = service.findById(id);

        // then
        assertAll(
            () -> assertThat(actual).isNotNull(),
            () -> assertThat(actual).extracting(Expense::getId).isEqualTo(id)
        );
    }

    @Test
    void givenInvalidIdWhenFindByIdThenNotFoundExceptionIsThrown() {
        // given
        long id = 999L;

        // when + then
        assertThrows(NotFoundException.class, () ->
            service.findById(id)
        );
    }

    @Test
    void givenNothingWhenFindAllThenAllExpensesAreReturned() {
        // when
        List<Expense> actual = service.findAll(new ExpenseSearchDto(null, null, null, null));

        // then
        assertThat(actual).hasSize(20);
    }

    @Test
    @DisplayName("Searching with specific parameters returns exactly one correct item")
    void givenValidSearchParametersWhenSearchExpensesThenReturnList() {
        // given
        ExpenseSearchDto searchParams = new ExpenseSearchDto("Dinner at Restaurant", 1L, 19.0, LocalDate.of(2022, 8, 18));

        double totalAmount = 100;
        WgDetailDto sharedFlat = new WgDetailDto();
        sharedFlat.setId(1L);

        List<DebitDto> debitUsers = new ArrayList<>();
        Set<ApplicationUser> usersOfFlat = sharedFlatRepository.findById(sharedFlat.getId()).orElseThrow().getUsers();
        usersOfFlat.forEach(user -> {
            UserListDto userDetailDto = UserListDtoBuilder.builder()
                .id(user.getId())
                .build();
            DebitDto debitDto = DebitDtoBuilder.builder()
                .user(userDetailDto)
                .splitBy(SplitBy.EQUAL)
                .value(totalAmount / usersOfFlat.size())
                .build();
            debitUsers.add(debitDto);
        });

        UserListDto paidBy = UserListDtoBuilder.builder()
            .id(usersOfFlat.stream().findAny().orElseThrow().getId())
            .build();

        ExpenseDto expenseDto = ExpenseDtoBuilder.builder()
            .title("Dinner at Restaurant")
            .description("Entertainment expenses")
            .amountInCents(1948.5)
            .createdAt(LocalDateTime.of(2022, 8, 18, 23, 35, 21))
            .paidBy(paidBy)
            .debitUsers(debitUsers)
            .build();

        // when
        List<Expense> actual = service.findAll(searchParams);

        // then
        assertAll(
            () -> assertThat(actual).isNotNull(),
            () -> assertThat(actual).isNotEmpty(),
            () -> assertThat(actual.get(0).getId()).isNotNull(),
            () -> assertThat(actual.get(0)).extracting(
                Expense::getTitle,
                Expense::getDescription,
                (Expense expense) -> Math.round(expense.getAmountInCents() * 10) / 10.0,
                Expense::getCreatedAt,
                expense -> expense.getPaidBy().getId(),
                expense -> expense.getDebitUsers().size()
            ).containsExactly(
                expenseDto.title(),
                expenseDto.description(),
                expenseDto.amountInCents(),
                expenseDto.createdAt(),
                expenseDto.paidBy().id(),
                expenseDto.debitUsers().size()
            )
        );
    }

    @Test
    void givenValidExpenseWhenCreateThenExpenseIsPersistedWithId() throws ValidationException, ConflictException, AuthenticationException {
        // given
        double totalAmount = 100;
        WgDetailDto sharedFlat = new WgDetailDto();
        sharedFlat.setId(1L);


        List<DebitDto> debitUsers = new ArrayList<>();
        Set<ApplicationUser> usersOfFlat = sharedFlatRepository.findById(sharedFlat.getId()).orElseThrow().getUsers();
        usersOfFlat.forEach(user -> {
            UserListDto userDetailDto = UserListDtoBuilder.builder()
                .id(user.getId())
                .build();
            DebitDto debitDto = DebitDtoBuilder.builder()
                .user(userDetailDto)
                .splitBy(SplitBy.EQUAL)
                .value(totalAmount / usersOfFlat.size())
                .build();
            debitUsers.add(debitDto);
        });

        UserListDto paidBy = UserListDtoBuilder.builder()
            .id(usersOfFlat.stream().findAny().orElseThrow().getId())
            .build();

        ExpenseDto expenseDto = ExpenseDtoBuilder.builder()
            .title("Test")
            .description("Test")
            .amountInCents(100.0)
            .createdAt(LocalDateTime.now())
            .paidBy(paidBy)
            .debitUsers(debitUsers)
            .build();

        // when
        Expense actual = service.create(expenseDto);

        // then
        service.findById(actual.getId());

        assertThat(actual.getDebitUsers()).hasSize(expenseDto.debitUsers().size());

        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual)
                .extracting(
                    Expense::getTitle,
                    Expense::getDescription,
                    Expense::getAmountInCents,
                    Expense::getDebitUsers
                ).contains(
                    expenseDto.title(),
                    expenseDto.description(),
                    expenseDto.amountInCents()
                ),
            () -> assertThat(actual.getPaidBy().getId()).isEqualTo(expenseDto.paidBy().id()),
            () -> assertThat(actual.getDebitUsers()).hasSize(expenseDto.debitUsers().size())
        );
    }

    static List<Arguments> data() {
        List<DebitDto> debitUsers = new ArrayList<>();
        UserListDto userDetailDto1 = UserListDtoBuilder.builder()
            .id(11L)
            .build();

        UserListDto userDetailDto2 = UserListDtoBuilder.builder()
            .id(16L)
            .build();

        UserListDto userDetailDto3 = UserListDtoBuilder.builder()
            .id(21L)
            .build();

        UserListDto userDetailDto4 = UserListDtoBuilder.builder()
            .id(1L)
            .build();

        return List.of(
            Arguments.of(
                List.of(
                    DebitDtoBuilder.builder()
                        .user(userDetailDto1)
                        .splitBy(SplitBy.EQUAL)
                        .value(25.0)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto2)
                        .splitBy(SplitBy.EQUAL)
                        .value(25.0)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto3)
                        .splitBy(SplitBy.EQUAL)
                        .value(25.0)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto4)
                        .splitBy(SplitBy.EQUAL)
                        .value(25.0)
                        .build()
                ),
                List.of(25.0, 25.0, 25.0, 25.0)
            ),
            Arguments.of(
                List.of(
                    DebitDtoBuilder.builder()
                        .user(userDetailDto1)
                        .splitBy(SplitBy.UNEQUAL)
                        .value(30.0)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto2)
                        .splitBy(SplitBy.UNEQUAL)
                        .value(30.0)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto3)
                        .splitBy(SplitBy.UNEQUAL)
                        .value(40.0)
                        .build()
                ),
                List.of(30.0, 30.0, 40.0)
            ),
            Arguments.of(
                List.of(
                    DebitDtoBuilder.builder()
                        .user(userDetailDto1)
                        .splitBy(SplitBy.PERCENTAGE)
                        .value(30.0)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto2)
                        .splitBy(SplitBy.PERCENTAGE)
                        .value(30.0)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto3)
                        .splitBy(SplitBy.PERCENTAGE)
                        .value(40.0)
                        .build()
                ),
                List.of(30.0, 30.0, 40.0)
            ),
            Arguments.of(
                List.of(
                    DebitDtoBuilder.builder()
                        .user(userDetailDto1)
                        .splitBy(SplitBy.PROPORTIONAL)
                        .value(5.0)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto2)
                        .splitBy(SplitBy.PROPORTIONAL)
                        .value(3.0)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto3)
                        .splitBy(SplitBy.PROPORTIONAL)
                        .value(1.0)
                        .build()
                ),
                List.of(55.55555555555556, 33.33333333333333, 11.11111111111111)
            )
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    void givenExpenseWithCertainSplitByWhenCreateThenAmountIsSplitCorrectly(List<DebitDto> debitDtos,
                                                                            List<Double> expected) throws ValidationException, ConflictException, AuthenticationException {
        // given
        double totalAmount = 100L;
        WgDetailDto sharedFlat = new WgDetailDto();
        sharedFlat.setId(applicationUser.getSharedFlat().getId());

        Set<ApplicationUser> usersOfFlat = sharedFlatRepository.findById(sharedFlat.getId()).orElseThrow().getUsers();

        UserListDto paidBy = UserListDtoBuilder.builder()
            .id(usersOfFlat.stream().findAny().orElseThrow().getId())
            .build();

        ExpenseDto expenseDto = ExpenseDtoBuilder.builder()
            .title("Test")
            .description("Test")
            .amountInCents(totalAmount)
            .createdAt(LocalDateTime.now())
            .paidBy(paidBy)
            .debitUsers(debitDtos)
            .build();


        // when
        Expense actual = service.create(expenseDto);

        // then
        assertAll(
            () -> assertThat(actual.getDebitUsers()).hasSize(expenseDto.debitUsers().size()),
            () -> assertThat(actual.getDebitUsers().stream().map((debit) ->
                debit.getPercent() / 100.0 * totalAmount
            ).toList()).isEqualTo(
                expected
            )
        );
    }

    @Test
    void givenInvalidExpenseWhenCreateThenValidationExceptionIsThrown() {
        // given
        ExpenseDto expenseDto = ExpenseDtoBuilder.builder()
            .title("")
            .description("")
            .amountInCents(100.0)
            .build();

        // when + then
        assertThrows(ValidationException.class, () ->
            service.create(expenseDto)
        );
    }

    @Test
    void givenExpenseWithDifferentSplitStrategiesWhenCreateThenConflictExceptionIsThrown() {
        // given
        UserListDto userDetailDto1 = UserListDtoBuilder.builder()
            .id(1L)
            .build();

        UserListDto userDetailDto2 = UserListDtoBuilder.builder()
            .id(2L)
            .build();

        UserListDto userDetailDto3 = UserListDtoBuilder.builder()
            .id(3L)
            .build();

        UserListDto userDetailDto4 = UserListDtoBuilder.builder()
            .id(4L)
            .build();

        UserListDto paidByConflict = UserListDtoBuilder.builder()
            .id(-999L)
            .build();

        ExpenseDto expenseDto = ExpenseDtoBuilder.builder()
            .title("Test")
            .description("Test")
            .amountInCents(100.0)
            .createdAt(LocalDateTime.now())
            .paidBy(paidByConflict)
            .debitUsers(
                List.of(
                    DebitDtoBuilder.builder()
                        .user(userDetailDto1)
                        .splitBy(SplitBy.EQUAL)
                        .value(25.0)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto2)
                        .splitBy(SplitBy.UNEQUAL)
                        .value(25.0)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto3)
                        .splitBy(SplitBy.EQUAL)
                        .value(25.0)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto4)
                        .splitBy(SplitBy.EQUAL)
                        .value(25.0)
                        .build()
                )
            )
            .build();

        // when + then
        assertThrows(ValidationException.class, () ->
            service.create(expenseDto)
        );
    }

    @Test
    void givenExpenseWithInvalidReferencesWhenCreateThenConflictExceptionIsThrown() {
        // given
        UserListDto userDetailDto1 = UserListDtoBuilder.builder()
            .id(1L)
            .build();

        UserListDto userDetailDto2 = UserListDtoBuilder.builder()
            .id(2L)
            .build();

        UserListDto userDetailDto3 = UserListDtoBuilder.builder()
            .id(3L)
            .build();

        UserListDto userDetailDto4 = UserListDtoBuilder.builder()
            .id(4L)
            .build();

        UserListDto paidByConflict = UserListDtoBuilder.builder()
            .id(-999L)
            .build();

        ExpenseDto expenseDto = ExpenseDtoBuilder.builder()
            .title("Test")
            .description("Test")
            .amountInCents(100.0)
            .createdAt(LocalDateTime.now())
            .paidBy(paidByConflict)
            .debitUsers(
                List.of(
                    DebitDtoBuilder.builder()
                        .user(userDetailDto1)
                        .splitBy(SplitBy.EQUAL)
                        .value(25.0)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto2)
                        .splitBy(SplitBy.EQUAL)
                        .value(25.0)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto3)
                        .splitBy(SplitBy.EQUAL)
                        .value(25.0)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto4)
                        .splitBy(SplitBy.EQUAL)
                        .value(25.0)
                        .build()
                )
            )
            .build();

        // when + then
        assertThrows(ConflictException.class, () ->
            service.create(expenseDto)
        );
    }


    @Test
    @DisplayName("Are debits calculated right, so that after paying the expense, the balance is 0?")
    void areDebitsCalculatedRight() {
        // given = debits defined via test data generator

        // when
        List<BalanceDebitDto> actual = service.calculateDebits();

        // then
        assertAll(
            () -> assertThat(actual).isNotNull(),
            () -> assertThat(actual).extracting(
                (BalanceDebitDto balanceDebitDto) -> balanceDebitDto.debtor().id(),
                (BalanceDebitDto balanceDebitDto) -> balanceDebitDto.creditor().id(),
                (BalanceDebitDto balanceDebitDto) -> Math.round(balanceDebitDto.valueInCent() * 10) / 10.0
            ).contains(
                new Tuple(11L, 6L, 791.8),
                new Tuple(1L, 6L, 331.7),
                new Tuple(16L, 21L, 153.9),
                new Tuple(16L, 6L, 115.4)
            )
        );
    }

    @Test
    @DisplayName("Are total expenses calculated right?")
    void areTotalExpensesCalculatedRight() {
        // given = expenses defined via test data generator

        // when
        List<UserValuePairDto> actual = service.calculateTotalExpensesPerUser();

        // then
        assertAll(
            () -> assertThat(actual).isNotNull(),
            () -> assertThat(actual).extracting(
                (UserValuePairDto userValuePairDto) -> userValuePairDto.user().id(),
                (UserValuePairDto userValuePairDto) -> Math.round((userValuePairDto.value()) * 10) / 10.0
            ).containsExactlyInAnyOrder(
                new Tuple(1L, 2549.4),
                new Tuple(21L, 1562.0),
                new Tuple(6L, 2952.8),
                new Tuple(11L, 745.7),
                new Tuple(16L, 0.0)
            )
        );
    }

    @Test
    @DisplayName("Are total debits calculated right?")
    void areTotalDebitsCalculatedRight() {
        // given = expenses defined via test data generator

        // when
        List<UserValuePairDto> actual = service.calculateTotalDebitsPerUser();

        // then
        assertAll(
            () -> assertThat(actual).isNotNull(),
            () -> assertThat(actual).extracting(
                (UserValuePairDto userValuePairDto) -> userValuePairDto.user().id(),
                (UserValuePairDto userValuePairDto) -> Math.round((userValuePairDto.value()) * 10) / 10.0
            ).containsExactlyInAnyOrder(
                new Tuple(1L, 2881.2),
                new Tuple(6L, 1713.9),
                new Tuple(11L, 1537.5),
                new Tuple(16L, 269.3),
                new Tuple(21L, 1408.0)
            )
        );
    }

    @Test
    @DisplayName("Are balances calculated right?")
    void areBalancesCalculatedRight() {
        // given = expenses defined via test data generator

        // when
        List<UserValuePairDto> actual = service.calculateBalancePerUser();

        // then
        assertAll(
            () -> assertThat(actual).isNotNull(),
            () -> assertThat(actual).extracting(
                (UserValuePairDto userValuePairDto) -> userValuePairDto.user().id(),
                (UserValuePairDto userValuePairDto) -> Math.round((userValuePairDto.value()) * 10) / 10.0
            ).containsExactlyInAnyOrder(
                new Tuple(1L, -331.7),
                new Tuple(21L, 153.9),
                new Tuple(6L, 1238.9),
                new Tuple(11L, -791.8),
                new Tuple(16L, -269.3)
            )
        );
    }

    @Test
    @DisplayName("Can an existing expense be deleted correctly?")
    void givenValidExpenseWhenDeleteThenExpenseIsDeleted() throws ValidationException, ConflictException, AuthenticationException {

        // given
        double totalAmount = 100;
        WgDetailDto sharedFlat = new WgDetailDto();
        sharedFlat.setId(1L);

        List<DebitDto> debitUsers = new ArrayList<>();
        Set<ApplicationUser> usersOfFlat = sharedFlatRepository.findById(sharedFlat.getId()).orElseThrow().getUsers();
        usersOfFlat.forEach(user -> {
            UserListDto userDetailDto = UserListDtoBuilder.builder()
                .id(user.getId())
                .build();
            DebitDto debitDto = DebitDtoBuilder.builder()
                .user(userDetailDto)
                .splitBy(SplitBy.EQUAL)
                .value(totalAmount / usersOfFlat.size())
                .build();
            debitUsers.add(debitDto);
        });

        UserListDto paidBy = UserListDtoBuilder.builder()
            .id(usersOfFlat.stream().findAny().orElseThrow().getId())
            .build();

        ExpenseDto expenseDto = ExpenseDtoBuilder.builder()
            .title("Test")
            .description("Test")
            .amountInCents(100.0)
            .createdAt(LocalDateTime.now())
            .paidBy(paidBy)
            .debitUsers(debitUsers)
            .build();

        Expense actual = service.create(expenseDto);

        // when
        service.delete(actual.getId());

        // then
        assertThrows(NotFoundException.class, () -> service.findById(actual.getId()));
    }

    @Test
    @DisplayName("It should not be possible to delete a non-existent expense.")
    void givenInvalidExpenseWhenDeleteThenNotFoundExceptionIsThrown() {
        // given
        Long invalidExpenseId = -20L;

        // when + then
        assertThrows(NotFoundException.class, () -> service.delete(invalidExpenseId));
    }

    @Test
    @DisplayName("Are repeating Expenses created correctly?")
    void createRepeatingExpense() throws ValidationException, ConflictException, AuthenticationException {
        // given
        ExpenseDto expenseDto = generateRepeatingExpense(2);

        // when
        Expense expense = service.create(expenseDto);

        // then
        Expense actual = service.findById(expense.getId());

        assertAll(
            () -> assertThat(actual).isNotNull(),
            () -> assertThat(actual).extracting(
                Expense::getId,
                Expense::getTitle,
                Expense::getDescription,
                Expense::getAmountInCents,
                (expense1 -> expense1.getPaidBy().getId()),
                Expense::getPeriodInDays
            ).contains(
                expense.getId(),
                expenseDto.title(),
                expenseDto.description(),
                expenseDto.amountInCents(),
                expenseDto.paidBy().id(),
                expenseDto.periodInDays()
            )
        );
    }

    @Test
    @DisplayName("Are repeating Expenses with negative days throwing exceptions?")
    void createInvalidRepeatingExpense() {
        // given
        ExpenseDto expenseDto = generateRepeatingExpense(-1);

        // when + then
        ValidationException e = assertThrows(ValidationException.class, () -> service.create(expenseDto));
        assertAll(
            () -> assertThat(e.errors().size()).isEqualTo(1),
            () -> assertThat(e.errors().get(0)).contains("greater")
        );
    }

    @Test
    @DisplayName("Is find all repeating Expenses Correct?")
    void findAllRepeatingExpenses() throws ValidationException, ConflictException, AuthenticationException {
        // given
        ExpenseDto expenseDto1 = generateRepeatingExpense(2);
        ExpenseDto expenseDto2 = generateRepeatingExpense(2);

        Expense expense1 = service.create(expenseDto1);
        Expense expense2 = service.create(expenseDto2);

        // when
        List<Expense> actual = service.findRepeatingExpenses();

        // then
        assertAll(
            () -> assertThat(actual).isNotNull(),
            () -> assertThat(actual).hasSize(2),
            () -> assertThat(actual).extracting(
                Expense::getId
            ).containsExactlyInAnyOrder(
                expense1.getId(),
                expense2.getId()
            )
        );
    }

    @Test
    @DisplayName("Is predefined repeating expense converted correctly")
    public void predefinedRepeatingExpenseWhenCreateThenPeriodInDaysIsCorrect() throws ValidationException, ConflictException, AuthenticationException {
        // given
        ExpenseDto expenseDto = generateRepeatingExpense(null, RepeatingExpenseType.FIRST_OF_MONTH);

        // when
        Expense expense = service.create(expenseDto);

        // then
        Expense actual = service.findById(expense.getId());

        assertAll(
            () -> assertThat(actual).isEqualTo(expense),
            () -> assertThat(actual).extracting(
                Expense::getPeriodInDays
            ).isEqualTo(
                RepeatingExpenseType.FIRST_OF_MONTH.value
            )
        );
    }

    private ExpenseDto generateRepeatingExpense(Integer days) {
        return this.generateRepeatingExpense(days, null);
    }

    private ExpenseDto generateRepeatingExpense(Integer days, RepeatingExpenseType typ) {

        List<UserListDto> users = this.applicationUser.getSharedFlat().getUsers().stream()
            .map(applicationUser1 -> userMapper.entityToUserListDto(applicationUser1))
            .toList();

        return ExpenseDtoBuilder.builder()
            .title("Test")
            .description("Test")
            .amountInCents(50.0)
            .createdAt(LocalDateTime.now())
            .paidBy(users.get(0))
            .isRepeating(true)
            .periodInDays(days)
            .repeatingExpenseType(typ)
            .debitUsers(
                List.of(
                    DebitDtoBuilder.builder()
                        .user(users.get(0))
                        .splitBy(SplitBy.EQUAL)
                        .value(25.0)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(users.get(1))
                        .splitBy(SplitBy.EQUAL)
                        .value(25.0)
                        .build()
                )
            )
            .build();
    }

}
