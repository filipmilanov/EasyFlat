package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
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

@Profile({"generateData", "test"})
@Component("ShoppingListDataGenerator")
@DependsOn({"CleanDatabase"})
public class ShoppingListDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ENTITIES_TO_GENERATE = 6;

    private final ShoppingListRepository shoppingListRepository;

    public ShoppingListDataGenerator(ShoppingListRepository shoppingListRepository) {
        this.shoppingListRepository = shoppingListRepository;
    }

    @PostConstruct
    public void generateShoppingLists() {
        for (int i = 0; i < NUMBER_OF_ENTITIES_TO_GENERATE; i += 2) {
            ShoppingList shoppingList = new ShoppingList();
            shoppingList.setName("Shopping List (Default)" + (i + 1));
            ShoppingList second = new ShoppingList();
            shoppingList.setName("Second" + (i + 2));

            for (int j = 0; j < NUMBER_OF_ENTITIES_TO_GENERATE / 2; j++) {
                SharedFlat sharedFlat = new SharedFlat();
                sharedFlat.setId((long) (j + 1));
                shoppingList.setSharedFlat(sharedFlat);
                second.setSharedFlat(sharedFlat);
            }

            shoppingListRepository.save(shoppingList);
            shoppingListRepository.save(second);
        }
    }
}