package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ExpenseDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ExpenseValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final Validator validator;

    public ExpenseValidator(Validator validator) {
        this.validator = validator;
    }

    public void validateExpense(ExpenseDto expenseDto,
                                List<ApplicationUser> applicationUsersOfFlat,
                                SharedFlat flatOfUser) throws ValidationException {
        LOGGER.trace("validateExpense({}, {}, {})", expenseDto, applicationUsersOfFlat, flatOfUser);

        validateExpenseDtoForCreate(expenseDto);

    }

    private void validateExpenseDtoForCreate(ExpenseDto expenseDto) throws ValidationException {
        LOGGER.trace("validateExpenseDtoForCreate({})", expenseDto);

        Set<ConstraintViolation<ExpenseDto>> validationViolations = validator.validate(expenseDto);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("The data is not valid", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }

    private void checkForConflict(ExpenseDto expenseDto,
                                  List<ApplicationUser> applicationUsers,
                                  SharedFlat flatOfUser) throws ConflictException {
        LOGGER.trace("checkForConflict({}, {}, {})", expenseDto, applicationUsers, flatOfUser);

        List<String> errors = new ArrayList<>();

        if (expenseDto.id() != null) {
            errors.add("The Id must be null");
        }

        if (expenseDto.paidBy() == null) {
            errors.add("The payerId must not be null");
        } else if (applicationUsers.stream()
            .noneMatch(user ->
                user.getId().equals(expenseDto.paidBy().getId())
            )) {
            errors.add("The payerId must be one of the flat members");
        }


        if (expenseDto.debitUsers() == null) {
            errors.add("The debitUsers must not be null");
        } else if (expenseDto.debitUsers().isEmpty()) {
            errors.add("The debitUsers must not be empty");
        } else if (expenseDto.debitUsers().stream()
            .anyMatch(user ->
                applicationUsers.stream()
                    .noneMatch(applicationUser ->
                        applicationUser.getId().equals(user.user().getId())
                    )
            )) {
            errors.add("The debitUsers must be one of the flat members");
        }

        if (expenseDto.sharedFlat() == null) {
            errors.add("The sharedFlat must not be null");
        } else if (!expenseDto.sharedFlat().getId().equals(flatOfUser.getId())) {
            errors.add("The sharedFlat must be the flat of the user");
        }

        if (!errors.isEmpty()) {
            throw new ConflictException("Conflict with persisted data", errors);
        }


    }
}
