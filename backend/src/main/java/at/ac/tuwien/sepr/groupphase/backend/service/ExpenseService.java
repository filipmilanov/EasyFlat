package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.BalanceDebitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.ExpenseDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

/**
 * Service for working with Expenses.
 */
public interface ExpenseService {

    /**
     * Finds an expense by id.
     *
     * @param id the id of the expense
     * @return the expense if found
     * @throws NotFoundException if the expense is not persisted
     */
    Expense findById(Long id) throws NotFoundException, AuthenticationException;

    /**
     * Creates a new expense.
     *
     * @param expenseDto the expense to create
     * @return the created expense with id
     * @throws ValidationException if the expense is not valid
     * @throws ConflictException   if the expense would produce an inconsistent state in the database
     */
    Expense create(ExpenseDto expenseDto) throws ValidationException, ConflictException, AuthenticationException;
}
