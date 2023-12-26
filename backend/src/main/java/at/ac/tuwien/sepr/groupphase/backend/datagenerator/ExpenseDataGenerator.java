package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Debit;
import at.ac.tuwien.sepr.groupphase.backend.entity.DebitKey;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.entity.SplitBy;
import at.ac.tuwien.sepr.groupphase.backend.repository.DebitRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Profile({"generateData", "test"})
@Component("ExpenseDataGenerator")
@DependsOn({"CleanDatabase", "SharedFlatDataGenerator", "ApplicationUserDataGenerator"})
public class ExpenseDataGenerator {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ENTITIES_TO_GENERATE = 20;
    private final Random random = new Random(25012024);
    private final ExpenseRepository expenseRepository;
    private final DebitRepository debitRepository;
    private final UserRepository userRepository;

    public ExpenseDataGenerator(ExpenseRepository expenseRepository,
                                DebitRepository debitRepository,
                                UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.debitRepository = debitRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void generateExpenses() {
        LOGGER.debug("generating {} Expenses", NUMBER_OF_ENTITIES_TO_GENERATE);

        for (int i = 0; i < NUMBER_OF_ENTITIES_TO_GENERATE; i++) {
            Expense expense = new Expense();
            expense.setTitle(generateRandomExpenseTitle());
            expense.setDescription(generateRandomExpenseDescription());
            expense.setAmountInCents(random.nextLong(2000));

            // Generate a random SplitBy option
            SplitBy splitBy = getRandomSplitByOption();
            expense.setSplitBy(splitBy);


            ApplicationUser paidByUser = new ApplicationUser();
            paidByUser.setId(random.nextLong(5) + 1);
            List<ApplicationUser> paidForUsers = new ArrayList<>(userRepository.findById(paidByUser.getId()).orElseThrow()
                .getSharedFlat()
                .getUsers()
            );

            expense.setPaidBy(paidByUser);

            // Generate participants and debits based on the selected SplitBy option
            generateRandomDebitUsers(paidForUsers, expense);


            SharedFlat sharedFlat = new SharedFlat();
            sharedFlat.setId(1L);
            expense.setSharedFlat(sharedFlat);

            expenseRepository.save(expense);
        }
    }

    private void generateRandomDebitUsers(List<ApplicationUser> paidForUsers, Expense expense) {
        List<Debit> debitUsers = new ArrayList<>();
        long remainingPercent = 100L;
        for (int i = 0; i < paidForUsers.size(); i++) {
            DebitKey debitKey = new DebitKey();
            debitKey.setExpense(expense);
            debitKey.setUser(paidForUsers.get(i));

            Debit debit = new Debit();
            debit.setId(debitKey);

            if (expense.getSplitBy() == SplitBy.EQUAL) {
                debit.setPercent(100L / paidForUsers.size());
            } else {
                long percent = (long) random.nextInt(100);
                if (remainingPercent < percent) {
                    percent = remainingPercent;
                }
                remainingPercent -= percent;

                debit.setPercent(percent);
            }
            debitUsers.add(debit);
        }
        expense.setDebitUsers(debitUsers);
    }

    private String generateRandomExpenseTitle() {
        String[] titles = {
            "Grocery Shopping",
            "Dinner at Restaurant",
            "Utility Bills",
            "Movie Night",
            "Home Supplies",
            "Party",
            "Cleaning Supplies"
        };
        return titles[random.nextInt(titles.length)];
    }

    private String generateRandomExpenseDescription() {
        String[] descriptions = {"Monthly grocery expenses", "Celebrating a special occasion", "Electricity and water bills", "Entertainment expenses", "Restocking home supplies"};
        return descriptions[random.nextInt(descriptions.length)];
    }

    private SplitBy getRandomSplitByOption() {
        SplitBy[] splitByOptions = SplitBy.values();
        return splitByOptions[random.nextInt(splitByOptions.length)];
    }

    private ApplicationUser getRandomParticipant(List<Debit> debits) {
        return debits.stream().findAny().orElseThrow().getId().getUser();
    }

}
