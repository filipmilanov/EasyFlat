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

@Profile({"generateData", "test", "unitTest"})
@Component("ShoppingListDataGenerator")
@DependsOn({"CleanDatabase"})
public class ShoppingListPresentationDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ENTITIES_TO_GENERATE = 5;

    private final ShoppingListRepository shoppingListRepository;

    public ShoppingListPresentationDataGenerator(ShoppingListRepository shoppingListRepository) {
        this.shoppingListRepository = shoppingListRepository;
    }

    @PostConstruct
    public void generateShoppingLists() {
        LOGGER.debug("generating {} ShoppingLists", NUMBER_OF_ENTITIES_TO_GENERATE);
        SharedFlat sharedFlat1 = new SharedFlat();
        sharedFlat1.setId(1L);

        // ShoppingList Default
        ShoppingList def = new ShoppingList();
        def.setName("Shopping List (Default)");
        def.setSharedFlat(sharedFlat1);
        shoppingListRepository.save(def);

        // ShoppingList 1: Tech
        ShoppingList techList = new ShoppingList();
        techList.setName("Tech");
        techList.setSharedFlat(sharedFlat1);
        shoppingListRepository.save(techList);

        // ShoppingList 2: Home Improvements
        SharedFlat sharedFlat2 = new SharedFlat();
        sharedFlat1.setId(2L);
        ShoppingList homeImprovementsList = new ShoppingList();
        homeImprovementsList.setName("Home Improvements");
        homeImprovementsList.setSharedFlat(sharedFlat2);
        shoppingListRepository.save(homeImprovementsList);

        // ShoppingList 3: Foodstuff
        ShoppingList foodstuffList = new ShoppingList();
        foodstuffList.setName("Foodstuff");
        foodstuffList.setSharedFlat(sharedFlat1);
        shoppingListRepository.save(foodstuffList);
    }
}
