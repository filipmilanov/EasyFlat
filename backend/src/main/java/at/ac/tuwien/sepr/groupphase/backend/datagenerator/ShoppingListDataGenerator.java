package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Profile({"test", "unitTest"})
@Component("ShoppingListDataGenerator")
@DependsOn({"CleanDatabase"})
public class ShoppingListDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ENTITIES_TO_GENERATE = 5;

    private final ShoppingListRepository shoppingListRepository;

    public ShoppingListDataGenerator(ShoppingListRepository shoppingListRepository) {
        this.shoppingListRepository = shoppingListRepository;
    }

    @PostConstruct
    public void generateShoppingLists() {
        LOGGER.debug("generating {} ShoppingLists", NUMBER_OF_ENTITIES_TO_GENERATE);
        for (int i = 0; i < NUMBER_OF_ENTITIES_TO_GENERATE; i++) {
            ShoppingList shoppingList = new ShoppingList();
            ShoppingList second = new ShoppingList();
            shoppingList.setName("Shopping List (Default)");
            second.setName("Second" + (i + 1));

            SharedFlat sharedFlat = new SharedFlat();
            sharedFlat.setId((long) (i + 1));
            shoppingList.setSharedFlat(sharedFlat);
            second.setSharedFlat(sharedFlat);

            shoppingListRepository.save(shoppingList);
            shoppingListRepository.save(second);
        }
    }
}