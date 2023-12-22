package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DebitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DebitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ExpenseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.DebitMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ExpenseMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Debit;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.SplitBy;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.ExpenseService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.authenticator.Authorization;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.ExpenseValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final ExpenseValidator expenseValidator;
    private final CustomUserDetailService customUserDetailService;
    private final DebitMapper debitMapper;
    private final Authorization authorization;
    private final AuthService authService;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository,
                              ExpenseMapper expenseMapper,
                              ExpenseValidator expenseValidator,
                              CustomUserDetailService customUserDetailService,
                              DebitMapper debitMapper,
                              Authorization authorization,
                              AuthService authService) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.expenseValidator = expenseValidator;
        this.customUserDetailService = customUserDetailService;
        this.debitMapper = debitMapper;
        this.authorization = authorization;
        this.authService = authService;
    }

    @Override
    public Expense findById(Long id) throws NotFoundException, AuthenticationException {
        LOGGER.info("findById: {}", id);

        Expense persistedExpense = expenseRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Expense not found"));

        List<ApplicationUser> allowedUsers = persistedExpense.getDebitUsers().stream().map(
            debit -> debit.getId().getUser()
        ).toList();
        allowedUsers.add(persistedExpense.getPaidBy());
        authorization.authenticateUser(
            allowedUsers.stream().map(ApplicationUser::getId).toList(),
            "User does not have access to this expense"
        );

        return persistedExpense;
    }

    @Override
    @Transactional
    public Expense create(ExpenseDto expenseDto) throws ValidationException, ConflictException, AuthenticationException {
        LOGGER.info("create: {}", expenseDto);

        ApplicationUser user = authService.getUserFromToken();
        List<ApplicationUser> usersOfFlat = user.getSharedFlat().getUsers().stream().toList();

        expenseValidator.validateExpense(expenseDto, usersOfFlat);

        authorization.authenticateUser(
            usersOfFlat.stream().map(ApplicationUser::getId).toList(),
            "You cannot create an expense for this flat"
        );

        List<Debit> debitList = defineDebitPerUserBySplitBy(
            expenseDto,
            expenseDto.debitUsers().stream().findAny().orElseThrow().splitBy()
        );

        Expense expense = expenseMapper.expenseDtoToExpense(expenseDto, debitList);

        expense.getDebitUsers().forEach(debit -> debit.getId().setExpense(expense));
        return expenseRepository.save(expense);
    }

    private List<Debit> defineDebitPerUserBySplitBy(ExpenseDto expense, SplitBy splitBy) throws ValidationException {
        LOGGER.trace("defineDebitPerUserBySplitBy({})", expense);

        List<Debit> debitList = new ArrayList<>();
        switch (splitBy) {
            case EQUAL, UNEQUAL -> expense.debitUsers().forEach(debitDto -> {
                DebitDto debit = DebitDtoBuilder.builder()
                    .splitBy(debitDto.splitBy())
                    .user(debitDto.user())
                    .value(debitDto.value() * 100.0 / expense.amountInCents())
                    .build();
                debitList.add(debitMapper.debitDtoToEntity(debit, expense));
            });
            case PERCENTAGE -> expense.debitUsers().forEach(debitDto -> {
                debitList.add(debitMapper.debitDtoToEntity(debitDto, expense));
            });
            case PROPORTIONAL -> {
                double proportions = expense.debitUsers().stream().mapToDouble(DebitDto::value).sum();
                double baseAmount = expense.amountInCents() / proportions;
                expense.debitUsers().forEach(debitDto -> {
                    double valueOfUser = baseAmount * debitDto.value();
                    DebitDto debit = DebitDtoBuilder.builder()
                        .splitBy(debitDto.splitBy())
                        .user(debitDto.user())
                        .value(valueOfUser * 100.0 / expense.amountInCents())
                        .build();
                    debitList.add(debitMapper.debitDtoToEntity(debit, expense));
                });
            }
            default ->
                throw new ValidationException("Unexpected value: " + splitBy, List.of("Unexpected value: " + splitBy));
        }
        return debitList;
    }
}
