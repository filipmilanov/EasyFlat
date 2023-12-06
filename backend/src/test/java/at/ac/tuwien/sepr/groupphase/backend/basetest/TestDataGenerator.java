package at.ac.tuwien.sepr.groupphase.backend.basetest;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.CleanDatabase;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.IngredientsDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.ItemDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.StorageDataGenerator;
import org.springframework.stereotype.Component;

@Component
public class TestDataGenerator {

    private final StorageDataGenerator digitalStorageDataGenerator;
    private final IngredientsDataGenerator ingredientsDataGenerator;
    private final ItemDataGenerator itemDataGenerator;
    private final CleanDatabase cleanDatabase;

    public TestDataGenerator(StorageDataGenerator digitalStorageDataGenerator, IngredientsDataGenerator ingredientsDataGenerator, ItemDataGenerator itemDataGenerator, CleanDatabase cleanDatabase) {
        this.digitalStorageDataGenerator = digitalStorageDataGenerator;
        this.ingredientsDataGenerator = ingredientsDataGenerator;
        this.itemDataGenerator = itemDataGenerator;
        this.cleanDatabase = cleanDatabase;
    }

    public void cleanUp() {
        cleanDatabase.truncateAllTablesAndRestartIds();
        digitalStorageDataGenerator.generateDigitalStorages();
        ingredientsDataGenerator.generateDigitalStorages();
        itemDataGenerator.generateDigitalStorages();
    }


}
