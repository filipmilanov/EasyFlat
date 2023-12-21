package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DebitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DebitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ExpenseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.DebitMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ExpenseMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Debit;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.entity.SplitBy;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ExpenseService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.ExpenseValidator;
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

    public ExpenseServiceImpl(ExpenseRepository expenseRepository,
                              ExpenseMapper expenseMapper,
                              ExpenseValidator expenseValidator,
                              CustomUserDetailService customUserDetailService,
                              DebitMapper debitMapper) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.expenseValidator = expenseValidator;
        this.customUserDetailService = customUserDetailService;
        this.debitMapper = debitMapper;
    }

    @Override
    public Expense findById(Long id, String jwt) throws NotFoundException {
        LOGGER.info("findById: {}", id);

        // TODO: authentication

        return expenseRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Expense not found"));
    }

    @Override
    public Expense create(ExpenseDto expenseDto, String jwt) throws ValidationException, ConflictException {
        LOGGER.info("create: {}", expenseDto);

        // TODO: authentication
        ApplicationUser user = customUserDetailService.getUser(jwt);

        SharedFlat sharedFlatOfUser = user.getSharedFlat();
        List<ApplicationUser> usersOfFlat = sharedFlatOfUser.getUsers().stream().toList();

        expenseValidator.validateExpense(expenseDto, usersOfFlat, sharedFlatOfUser);

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
