package at.ac.tuwien.sepr.groupphase.backend.basetest;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.ApplicationUserDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.CleanDatabase;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.EventDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.IngredientsDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.ItemDataGenerator;
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
    private final UnitDataGenerator unitDataGenerator;
    private final ApplicationUserDataGenerator applicationUserDataGenerator;
    private final SharedFlatDataGenerator sharedFlatDataGenerator;

    private final EventDataGenerator eventDataGenerator;

    public TestDataGenerator(StorageDataGenerator digitalStorageDataGenerator,
                             IngredientsDataGenerator ingredientsDataGenerator,
                             ItemDataGenerator itemDataGenerator,
                             CleanDatabase cleanDatabase,
                             UnitDataGenerator unitDataGenerator,
                             ApplicationUserDataGenerator applicationUserDataGenerator,
                             SharedFlatDataGenerator sharedFlatDataGenerator, EventDataGenerator eventDataGenerator) {
        this.digitalStorageDataGenerator = digitalStorageDataGenerator;
        this.ingredientsDataGenerator = ingredientsDataGenerator;
        this.itemDataGenerator = itemDataGenerator;
        this.cleanDatabase = cleanDatabase;
        this.applicationUserDataGenerator = applicationUserDataGenerator;
        this.sharedFlatDataGenerator = sharedFlatDataGenerator;
        this.unitDataGenerator = unitDataGenerator;
        this.eventDataGenerator = eventDataGenerator;
    }

    public void cleanUp() throws ValidationException, ConflictException {
        cleanDatabase.truncateAllTablesAndRestartIds();
        sharedFlatDataGenerator.generateDigitalStorages();
        applicationUserDataGenerator.generateDigitalStorages();
        digitalStorageDataGenerator.generateDigitalStorages();
        ingredientsDataGenerator.generateDigitalStorages();
        unitDataGenerator.generate();
        itemDataGenerator.generateDigitalStorages();
        eventDataGenerator.generateEvents();
    }


}
