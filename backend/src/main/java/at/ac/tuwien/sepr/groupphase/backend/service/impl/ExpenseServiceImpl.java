package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ExpenseDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.ExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public Expense findById(Long id) throws NotFoundException {
        return null;
    }

    @Override
    public Expense create(ExpenseDto expenseDto) throws ValidationException, ConflictException {
        return null;
    }
}
