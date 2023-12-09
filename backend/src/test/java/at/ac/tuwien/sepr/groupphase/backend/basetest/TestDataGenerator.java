package at.ac.tuwien.sepr.groupphase.backend.basetest;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.ApplicationUserDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.CleanDatabase;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.ExpenseDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.IngredientsDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.ItemDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.SharedFlatDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.StorageDataGenerator;
import org.springframework.stereotype.Component;

@Component
public class TestDataGenerator {

    private final StorageDataGenerator digitalStorageDataGenerator;
    private final IngredientsDataGenerator ingredientsDataGenerator;
    private final ItemDataGenerator itemDataGenerator;
    private final CleanDatabase cleanDatabase;
    private final ApplicationUserDataGenerator applicationUserDataGenerator;
    private final SharedFlatDataGenerator sharedFlatDataGenerator;
    private final ExpenseDataGenerator expenseDataGenerator;

    public TestDataGenerator(StorageDataGenerator digitalStorageDataGenerator,
                             IngredientsDataGenerator ingredientsDataGenerator,
                             ItemDataGenerator itemDataGenerator,
                             CleanDatabase cleanDatabase,
                             ApplicationUserDataGenerator applicationUserDataGenerator,
                             SharedFlatDataGenerator sharedFlatDataGenerator,
                             ExpenseDataGenerator expenseDataGenerator) {
        this.digitalStorageDataGenerator = digitalStorageDataGenerator;
        this.ingredientsDataGenerator = ingredientsDataGenerator;
        this.itemDataGenerator = itemDataGenerator;
        this.cleanDatabase = cleanDatabase;
        this.applicationUserDataGenerator = applicationUserDataGenerator;
        this.sharedFlatDataGenerator = sharedFlatDataGenerator;
        this.expenseDataGenerator = expenseDataGenerator;
    }

    public void cleanUp() {
        cleanDatabase.truncateAllTablesAndRestartIds();
        sharedFlatDataGenerator.generateSharedFlats();
        applicationUserDataGenerator.generateApplicationUsers();
        digitalStorageDataGenerator.generateDigitalStorages();
        ingredientsDataGenerator.generateIngredients();
        itemDataGenerator.generateItems();
        expenseDataGenerator.generateExpenses();
    }


}
