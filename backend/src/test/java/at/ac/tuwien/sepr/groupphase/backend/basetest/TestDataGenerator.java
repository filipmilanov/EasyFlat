package at.ac.tuwien.sepr.groupphase.backend.basetest;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.ApplicationUserDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.CleanDatabase;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.CookbookDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.EventDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.ExpenseDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.IngredientsDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.ItemDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.RecipeDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.SharedFlatDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.StorageDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.UnitDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class TestDataGenerator {

    private final StorageDataGenerator digitalStorageDataGenerator;
    private final IngredientsDataGenerator ingredientsDataGenerator;
    private final ItemDataGenerator itemDataGenerator;
    private final CleanDatabase cleanDatabase;
    private final ApplicationUserDataGenerator applicationUserDataGenerator;
    private final SharedFlatDataGenerator sharedFlatDataGenerator;
    private final RecipeDataGenerator recipeDataGenerator;
    private final CookbookDataGenerator cookbookDataGenerator;
    private final EventDataGenerator eventDataGenerator;
    private final UnitDataGenerator unitDataGenerator;
    private final ExpenseDataGenerator expenseDataGenerator;

    public TestDataGenerator(StorageDataGenerator digitalStorageDataGenerator,
                             IngredientsDataGenerator ingredientsDataGenerator,
                             ItemDataGenerator itemDataGenerator,
                             CleanDatabase cleanDatabase,
                             UnitDataGenerator unitDataGenerator,
                             ApplicationUserDataGenerator applicationUserDataGenerator,
                             SharedFlatDataGenerator sharedFlatDataGenerator,
                             ExpenseDataGenerator expenseDataGenerator,
                             RecipeDataGenerator recipeDataGenerator,
                             CookbookDataGenerator cookbookDataGenerator, EventDataGenerator eventDataGenerator) {
        this.digitalStorageDataGenerator = digitalStorageDataGenerator;
        this.ingredientsDataGenerator = ingredientsDataGenerator;
        this.itemDataGenerator = itemDataGenerator;
        this.cleanDatabase = cleanDatabase;
        this.applicationUserDataGenerator = applicationUserDataGenerator;
        this.sharedFlatDataGenerator = sharedFlatDataGenerator;
        this.unitDataGenerator = unitDataGenerator;
        this.recipeDataGenerator = recipeDataGenerator;
        this.cookbookDataGenerator = cookbookDataGenerator;
        this.eventDataGenerator = eventDataGenerator;
        this.expenseDataGenerator = expenseDataGenerator;
    }

    public void cleanUp() throws ValidationException, ConflictException {
        cleanDatabase.truncateAllTablesAndRestartIds();
        unitDataGenerator.generate();
        sharedFlatDataGenerator.generateSharedFlats();
        applicationUserDataGenerator.generateApplicationUsers();
        digitalStorageDataGenerator.generateDigitalStorages();
        cookbookDataGenerator.generateCookbooks();
        recipeDataGenerator.generateItems();
        eventDataGenerator.generateEvents();
        ingredientsDataGenerator.generateIngredients();
        itemDataGenerator.generateItems();
        expenseDataGenerator.generateExpenses();
    }


}
