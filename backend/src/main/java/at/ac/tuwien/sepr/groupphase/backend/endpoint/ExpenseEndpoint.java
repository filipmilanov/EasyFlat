package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ExpenseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ExpenseMapper;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.ExpenseService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/expense")
public class ExpenseEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ExpenseService expenseService;
    private final ExpenseMapper expenseMapper;

    public ExpenseEndpoint(ExpenseService expenseService, ExpenseMapper expenseMapper) {
        this.expenseService = expenseService;
        this.expenseMapper = expenseMapper;
    }

    @Secured("ROLE_USER")
    @GetMapping("{id}")
    public ExpenseDto findById(@PathVariable("id") Long id, @RequestHeader("Authorization") String jwt) {
        LOGGER.info("findById: {}", id);

        return expenseMapper.entityToExpenseDto(
            expenseService.findById(id, jwt)
        );
    }

    @PermitAll
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseDto create(@RequestBody ExpenseDto expenseDto, @RequestHeader("Authorization") String jwt) throws ValidationException, ConflictException {
        LOGGER.info("create: {}", expenseDto);

        return expenseMapper.entityToExpenseDto(
            expenseService.create(expenseDto, jwt)
        );
    }

}
