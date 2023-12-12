package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ExpenseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ExpenseMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
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
import java.util.List;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final ExpenseValidator expenseValidator;
    private final CustomUserDetailService customUserDetailService;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository,
                              ExpenseMapper expenseMapper,
                              ExpenseValidator expenseValidator,
                              CustomUserDetailService customUserDetailService) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.expenseValidator = expenseValidator;
        this.customUserDetailService = customUserDetailService;
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

        Expense expense = expenseMapper.expenseDtoToExpense(expenseDto);
        expense.getDebitUsers().forEach(debit -> debit.getId().setExpense(expense));
        return expenseRepository.save(expense);
    }
}
