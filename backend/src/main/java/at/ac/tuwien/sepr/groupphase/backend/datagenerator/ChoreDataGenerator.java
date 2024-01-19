package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChoreRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Profile({"generateData", "test"})
@Component("ShoppingListDataGenerator")
@DependsOn({"CleanDatabase"})
public class ChoreDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ENTITIES_TO_GENERATE = 5;
    private final ChoreRepository choreRepository;

    public ChoreDataGenerator(ChoreRepository choreRepository) {
        this.choreRepository = choreRepository;
    }

    @PostConstruct
    public void generateChores() {
        for (int i = 0; i < NUMBER_OF_ENTITIES_TO_GENERATE; i++) {

        }
    }
}
