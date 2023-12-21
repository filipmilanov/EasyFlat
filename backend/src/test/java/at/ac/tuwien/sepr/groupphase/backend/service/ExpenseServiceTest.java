package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DebitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DebitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ExpenseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ExpenseDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDtoBuilder;
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

import java.time.LocalDateTime;
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
        assertAll(
            () -> assertThat(actual).isNotNull();
            () -> assertThat(actual.getId()).isEqualTo(id);
        );
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
        assertThat(actual.getPaidBy().getId()).isEqualTo(expenseDto.paidBy().id());
        assertThat(actual.getSharedFlat().getId()).isEqualTo(expenseDto.sharedFlat().getId());
        assertThat(actual.getDebitUsers()).hasSize(expenseDto.debitUsers().size());
    }

    static List<Arguments> data() {
        List<DebitDto> debitUsers = new ArrayList<>();
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
                                                                            List<Double> expected) throws ValidationException, ConflictException {
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
            .sharedFlat(sharedFlat)
            .build();


        // when
        Expense actual = service.create(expenseDto, "Bearer Token");

        // then
        assertThat(actual.getDebitUsers()).hasSize(expenseDto.debitUsers().size());
        assertThat(actual.getDebitUsers().stream().map((debit) ->
            debit.getPercent() / 100.0 * totalAmount
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
            .amountInCents(100.0)
            .build();

        // when + then
        assertThrows(ValidationException.class, () ->
            service.create(expenseDto, "Bearer Token")
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
            service.create(expenseDto, "Bearer Token")
        );
    }
}