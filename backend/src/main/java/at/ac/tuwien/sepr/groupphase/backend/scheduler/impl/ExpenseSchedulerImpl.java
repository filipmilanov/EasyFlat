package at.ac.tuwien.sepr.groupphase.backend.scheduler.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.ExpenseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ExpenseMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.scheduler.ExpenseScheduler;
import at.ac.tuwien.sepr.groupphase.backend.service.ExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ExpenseSchedulerImpl implements ExpenseScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ExpenseService expenseService;
    private final ExpenseMapper expenseMapper;

    public ExpenseSchedulerImpl(ExpenseService expenseService,
                                ExpenseMapper expenseMapper) {
        this.expenseService = expenseService;
        this.expenseMapper = expenseMapper;
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void createRepeatingExpense() {
        LOGGER.info("Creating repeating expenses");

        List<Expense> repeatingExpenses = expenseService.findRepeatingExpenses();

        repeatingExpenses.stream().filter(this::shouldCreateExpenseToday).forEach(expense -> {
            LOGGER.info("Creating expense for {}", expense.getTitle());
            try {
                expenseService.create(convertToNewExpense(expense));
            } catch (ValidationException e) {
                LOGGER.warn("Could not create repeating expense for {}, because of a ValidationException", expense, e);
            } catch (ConflictException e) {
                LOGGER.warn("Could not create repeating expense for {}, because of a ConflictException", expense, e);
            } catch (AuthenticationException e) {
                LOGGER.warn("Could not create repeating expense for {}, because of a AuthenticationException", expense, e);
            }
        });
    }

    private boolean shouldCreateExpenseToday(Expense expense) {
        return expense.getCreatedAt().plusDays(expense.getPeriodInDays()).toLocalDate().isEqual(LocalDate.now());
    }

    private ExpenseDto convertToNewExpense(Expense expense) {
        expense.setCreatedAt(LocalDateTime.now());
        return expenseMapper.entityToExpenseDto(expense);
    }
}
