package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DebitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DebitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ExpenseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ExpenseDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.SplitBy;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.SharedFlatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

    @MockBean
    private CustomUserDetailService customUserDetailService;

    private ApplicationUser applicationUser;

    @BeforeEach
    public void cleanUp() {
        testDataGenerator.cleanUp();

        applicationUser = userRepository.findById(1L).orElseThrow();
        when(customUserDetailService.getUser(any(String.class))).thenReturn(applicationUser);
    }

    @Test
    void givenValidIdWhenFindByIdThenExpenseWithCorrectIdIsReturned() throws ValidationException, ConflictException {
        // given
        long id = 1L;

        // when
        Expense actual = service.findById(id, "Bearer Token");

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(id);
    }

    @Test
    void givenInvalidIdWhenFindByIdThenNotFoundExceptionIsThrown() {
        // given
        long id = 999L;

        // when + then
        assertThrows(NotFoundException.class, () ->
            service.findById(id, "Bearer Token")
        );
    }

    @Test
    void givenValidExpenseWhenCreateThenExpenseIsPersistedWithId() throws ValidationException, ConflictException {
        // given
        long totalAmount = 100L;
        WgDetailDto sharedFlat = new WgDetailDto();
        sharedFlat.setId(1L);


        List<DebitDto> debitUsers = new ArrayList<>();
        Set<ApplicationUser> usersOfFlat = sharedFlatRepository.findById(sharedFlat.getId()).orElseThrow().getUsers();
        usersOfFlat.forEach(user -> {
            UserDetailDto userDetailDto = new UserDetailDto();
            userDetailDto.setId(user.getId());
            DebitDto debitDto = DebitDtoBuilder.builder()
                .user(userDetailDto)
                .splitBy(SplitBy.EQUAL)
                .value(totalAmount / usersOfFlat.size())
                .build();
            debitUsers.add(debitDto);
        });

        UserDetailDto paidBy = new UserDetailDto();
        paidBy.setId(usersOfFlat.stream().findAny().orElseThrow().getId());

        ExpenseDto expenseDto = ExpenseDtoBuilder.builder()
            .title("Test")
            .description("Test")
            .amountInCents(100L)
            .paidBy(paidBy)
            .debitUsers(debitUsers)
            .sharedFlat(sharedFlat)
            .build();

        // when
        Expense actual = service.create(expenseDto, "Bearer Token");

        // then
        service.findById(actual.getId(), "Bearer Token");

        assertThat(actual.getId()).isNotNull();
        assertThat(actual)
            .extracting(
                Expense::getTitle,
                Expense::getDescription,
                Expense::getAmountInCents,
                Expense::getDebitUsers
            ).contains(
                expenseDto.title(),
                expenseDto.description(),
                expenseDto.amountInCents()
            );
        assertThat(actual.getPaidBy().getId()).isEqualTo(expenseDto.paidBy().getId());
        assertThat(actual.getSharedFlat().getId()).isEqualTo(expenseDto.sharedFlat().getId());
        assertThat(actual.getDebitUsers()).hasSize(expenseDto.debitUsers().size());
    }

    static List<Arguments> data() {
        List<DebitDto> debitUsers = new ArrayList<>();
        UserDetailDto userDetailDto1 = new UserDetailDto();
        userDetailDto1.setId(1L);

        UserDetailDto userDetailDto2 = new UserDetailDto();
        userDetailDto2.setId(2L);

        UserDetailDto userDetailDto3 = new UserDetailDto();
        userDetailDto3.setId(3L);

        UserDetailDto userDetailDto4 = new UserDetailDto();
        userDetailDto4.setId(4L);

        return List.of(
            Arguments.of(
                List.of(
                    DebitDtoBuilder.builder()
                        .user(userDetailDto1)
                        .splitBy(SplitBy.EQUAL)
                        .value(25L)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto2)
                        .splitBy(SplitBy.EQUAL)
                        .value(25L)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto3)
                        .splitBy(SplitBy.EQUAL)
                        .value(25L)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto4)
                        .splitBy(SplitBy.EQUAL)
                        .value(25L)
                        .build()
                ),
                List.of(25L, 25L, 25L, 25L)
            ),
            Arguments.of(
                List.of(
                    DebitDtoBuilder.builder()
                        .user(userDetailDto1)
                        .splitBy(SplitBy.UNEQUAL)
                        .value(30L)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto2)
                        .splitBy(SplitBy.UNEQUAL)
                        .value(30L)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto3)
                        .splitBy(SplitBy.UNEQUAL)
                        .value(40L)
                        .build()
                ),
                List.of(30L, 30L, 40L)
            ),
            Arguments.of(
                List.of(
                    DebitDtoBuilder.builder()
                        .user(userDetailDto1)
                        .splitBy(SplitBy.PERCENTAGE)
                        .value(30L)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto2)
                        .splitBy(SplitBy.PERCENTAGE)
                        .value(30L)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto3)
                        .splitBy(SplitBy.PERCENTAGE)
                        .value(40L)
                        .build()
                ),
                List.of(30L, 30L, 40L)
            ),
            Arguments.of(
                List.of(
                    DebitDtoBuilder.builder()
                        .user(userDetailDto1)
                        .splitBy(SplitBy.PROPORTIONAL)
                        .value(5L)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto2)
                        .splitBy(SplitBy.PROPORTIONAL)
                        .value(3L)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto3)
                        .splitBy(SplitBy.PROPORTIONAL)
                        .value(1L)
                        .build()
                ),
                List.of(56L, 33L, 11L)
            )
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    void givenExpenseWithCertainSplitByWhenCreateThenAmountIsSplitCorrectly(List<DebitDto> debitDtos,
                                                                            List<Long> expected) throws ValidationException, ConflictException {
        // given
        long totalAmount = 100L;
        WgDetailDto sharedFlat = new WgDetailDto();
        sharedFlat.setId(applicationUser.getSharedFlat().getId());

        Set<ApplicationUser> usersOfFlat = sharedFlatRepository.findById(sharedFlat.getId()).orElseThrow().getUsers();

        UserDetailDto paidBy = new UserDetailDto();
        paidBy.setId(usersOfFlat.stream().findAny().orElseThrow().getId());

        ExpenseDto expenseDto = ExpenseDtoBuilder.builder()
            .title("Test")
            .description("Test")
            .amountInCents(100L)
            .paidBy(paidBy)
            .debitUsers(debitDtos)
            .sharedFlat(sharedFlat)
            .build();


        // when
        Expense actual = service.create(expenseDto, "Bearer Token");

        // then
        assertThat(actual.getDebitUsers()).hasSize(expenseDto.debitUsers().size());
        assertThat(actual.getDebitUsers().stream().map((debit) ->
            debit.getPercent() * totalAmount
        ).toList()).isEqualTo(
            expected
        );
    }

    @Test
    void givenInvalidExpenseWhenCreateThenValidationExceptionIsThrown() {
        // given
        ExpenseDto expenseDto = ExpenseDtoBuilder.builder()
            .title("")
            .description("")
            .amountInCents(100L)
            .build();

        // when + then
        assertThrows(ValidationException.class, () ->
            service.create(expenseDto, "Bearer Token")
        );
    }

    static List<Arguments> givenExpenseWithInvalidReferencesWhenCreateThenConflictExceptionIsThrownData() {
        UserDetailDto paidBy = new UserDetailDto();
        paidBy.setId(1L);

        UserDetailDto paidByConflict = new UserDetailDto();
        paidByConflict.setId(999L);

        WgDetailDto sharedFlat = new WgDetailDto();
        sharedFlat.setId(1L);

        WgDetailDto sharedFlatConflict = new WgDetailDto();
        sharedFlatConflict.setId(999L);

        return List.of(
            Arguments.of(
                paidByConflict,
                sharedFlat
            ),
            Arguments.of(
                paidBy,
                sharedFlatConflict
            )
        );

    }


    @ParameterizedTest
    @MethodSource("givenExpenseWithInvalidReferencesWhenCreateThenConflictExceptionIsThrownData")
    void givenExpenseWithInvalidReferencesWhenCreateThenConflictExceptionIsThrown(UserDetailDto paidBy,
                                                                                  WgDetailDto sharedFlat) {
        // given
        UserDetailDto userDetailDto1 = new UserDetailDto();
        userDetailDto1.setId(1L);

        UserDetailDto userDetailDto2 = new UserDetailDto();
        userDetailDto2.setId(2L);

        UserDetailDto userDetailDto3 = new UserDetailDto();
        userDetailDto3.setId(3L);

        UserDetailDto userDetailDto4 = new UserDetailDto();
        userDetailDto4.setId(4L);


        ExpenseDto expenseDto = ExpenseDtoBuilder.builder()
            .title("Test")
            .description("Test")
            .amountInCents(100L)
            .paidBy(paidBy)
            .debitUsers(
                List.of(
                    DebitDtoBuilder.builder()
                        .user(userDetailDto1)
                        .splitBy(SplitBy.EQUAL)
                        .value(25L)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto2)
                        .splitBy(SplitBy.EQUAL)
                        .value(25L)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto3)
                        .splitBy(SplitBy.EQUAL)
                        .value(25L)
                        .build(),
                    DebitDtoBuilder.builder()
                        .user(userDetailDto4)
                        .splitBy(SplitBy.EQUAL)
                        .value(25L)
                        .build()
                )
            )
            .sharedFlat(sharedFlat)
            .build();

        // when + then
        assertThrows(ConflictException.class, () ->
            service.create(expenseDto, "Bearer Token")
        );
    }
}