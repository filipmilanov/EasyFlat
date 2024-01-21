package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemCache;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Profile({"generateData", "test"})
@Component("ShoppingItemDataGenerator")
@DependsOn({"CleanDatabase"})
public class ShoppingItemDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ENTITIES_TO_GENERATE = 5;
    private final ShoppingItemRepository shoppingItemRepository;

    public ShoppingItemDataGenerator(ShoppingItemRepository shoppingItemRepository) {
        this.shoppingItemRepository = shoppingItemRepository;
    }

    @PostConstruct
    public void generateShoppingItems() {
        LOGGER.debug("generating {} ShoppingItems", NUMBER_OF_ENTITIES_TO_GENERATE);

        for (int i = 0; i < NUMBER_OF_ENTITIES_TO_GENERATE; i++) {
            ShoppingItem shoppingItem = new ShoppingItem();
            //shoppingItem.setLabels(labels);
            shoppingItem.setBoughtAt("billa");
            shoppingItem.setAlwaysInStock(false);
            shoppingItem.setPriceInCent(210L);
            shoppingItem.setQuantityCurrent(3.0);
            DigitalStorage digitalStorage = new DigitalStorage();
            digitalStorage.setStorageId(1L);
            //ItemCache itemCache = getItemCache(country);
            //shoppingItem.setItemCache(itemCache);
            ShoppingList shoppingList = new ShoppingList();
            shoppingList.setId(1L);
            shoppingList.setName("Shopping List (Default)");
            shoppingItem.setShoppingList(shoppingList);
        }
    }
}