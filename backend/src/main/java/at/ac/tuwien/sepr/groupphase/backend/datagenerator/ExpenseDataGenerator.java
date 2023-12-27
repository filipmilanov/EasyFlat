package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Debit;
import at.ac.tuwien.sepr.groupphase.backend.entity.DebitKey;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.SplitBy;
import at.ac.tuwien.sepr.groupphase.backend.repository.DebitRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Profile({"generateData", "test"})
@Component("ExpenseDataGenerator")
@DependsOn({"CleanDatabase", "SharedFlatDataGenerator", "ApplicationUserDataGenerator"})
public class ExpenseDataGenerator {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ENTITIES_TO_GENERATE = 5;
    private final ExpenseRepository expenseRepository;
    private final DebitRepository debitRepository;

    public ExpenseDataGenerator(ExpenseRepository expenseRepository,
                                DebitRepository debitRepository) {
        this.expenseRepository = expenseRepository;
        this.debitRepository = debitRepository;
    }

    @PostConstruct
    public void generateExpenses() {
        LOGGER.debug("generating {} Expenses ", NUMBER_OF_ENTITIES_TO_GENERATE);
        for (int i = 0; i < NUMBER_OF_ENTITIES_TO_GENERATE; i++) {
            Expense expense = new Expense();
            expense.setTitle("Expense " + i);
            expense.setDescription("Description " + i);
            expense.setSplitBy(SplitBy.EQUAL);
            expense.setAmountInCents(1000.0);


            for (int j = 0; j < NUMBER_OF_ENTITIES_TO_GENERATE; j++) {
                ApplicationUser user = new ApplicationUser();
                user.setId((long) (j + 1));

                DebitKey debitKey = new DebitKey();
                debitKey.setExpense(expense);
                debitKey.setUser(user);

                Debit debit = new Debit();
                debit.setPercent(100.0 / NUMBER_OF_ENTITIES_TO_GENERATE);
                debit.setId(debitKey);
                expense.getDebitUsers().add(debit);
            }

            ApplicationUser user = new ApplicationUser();
            user.setId((long) (i + 1));

            expense.setPaidBy(user);

            expenseRepository.save(expense);
        }
    }
}
