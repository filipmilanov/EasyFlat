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
import java.util.LinkedList;
import java.util.List;

@Profile({"generateData"})
@Component("ShoppingListDataGenerator")
@DependsOn({"CleanDatabase", "SharedFlatDataGenerator"})
public class ShoppingListDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ENTITIES_TO_GENERATE = 5;
    private final ShoppingListRepository shoppingListRepository;

    public ShoppingListDataGenerator(ShoppingListRepository shoppingListRepository) {
        this.shoppingListRepository = shoppingListRepository;
    }

    @PostConstruct
    public void generateShoppingLists(long sharedFlatId) {
        LOGGER.debug("generating {} Shopping Lists", NUMBER_OF_ENTITIES_TO_GENERATE);
        List<String> names = this.getShoppingListNames();
        for (int i = 0; i < NUMBER_OF_ENTITIES_TO_GENERATE; i++) {
            ShoppingList shoppingList = new ShoppingList();
            shoppingList.setName(names.get(i));

            SharedFlat sharedFlat = new SharedFlat();
            sharedFlat.setId(sharedFlatId);

            shoppingList.setSharedFlat(sharedFlat);

            shoppingListRepository.save(shoppingList);
        }
    }

    public List<String> getShoppingListNames() {
        List<String> names = new LinkedList<>();
        names.add("Groceries");
        names.add("Weekly Meal Plan");
        names.add("Personal Care Items");
        names.add("Tech Gadgets Wishlist");
        names.add("Home Improvement");
        return names;
    }
}
