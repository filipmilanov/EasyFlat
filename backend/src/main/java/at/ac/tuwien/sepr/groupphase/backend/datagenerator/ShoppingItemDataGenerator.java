package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemLabel;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UnitRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Profile({"generateData"})
@Component("ItemDataGenerator")
@DependsOn({
    "CleanDatabase",
    "StorageDataGenerator",
    "IngredientsDataGenerator",
    "UnitDataGenerator"})
public class ShoppingItemDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ENTITIES_TO_GENERATE = 5;
    private final UnitRepository unitRepository;
    private final ShoppingListDataGenerator shoppingListDataGenerator;
    private final ShoppingItemRepository shoppingItemRepository;

    public ShoppingItemDataGenerator(UnitRepository unitRepository, ShoppingListDataGenerator shoppingListDataGenerator, ShoppingItemRepository shoppingItemRepository) {

        this.unitRepository = unitRepository;
        this.shoppingListDataGenerator = shoppingListDataGenerator;
        this.shoppingItemRepository = shoppingItemRepository;
    }

    @PostConstruct
    public void generateShoppingItems(List<String> shoppingListNames) {
        LOGGER.debug("generating {} Shopping Items ", NUMBER_OF_ENTITIES_TO_GENERATE);
        Unit kg = unitRepository.findByName("kg").orElseThrow();
        List<String> generalNames = shoppingListDataGenerator.getShoppingListNames();

        for (int i = 0; i < NUMBER_OF_ENTITIES_TO_GENERATE; i++) {
            ShoppingItem shoppingItem = new ShoppingItem();
            shoppingItem.setEan("1234567890123");
            shoppingItem.setGeneralName("Grocery Item");
            shoppingItem.setProductName("Product ABC");
            shoppingItem.setBrand("Brand XYZ");
            shoppingItem.setQuantityCurrent(5L);
            shoppingItem.setQuantityTotal(10L);
            shoppingItem.setAlwaysIsStock(i % 2 == 0);
            shoppingItem.setMinimumQuantity(i % 2 == 0 ? 2L : null);

            shoppingItem.setUnit(kg);

            shoppingItem.setExpireDate(LocalDate.now().plusMonths(3));
            shoppingItem.setDescription("Description of the product");
            shoppingItem.setPriceInCent(499L);
            shoppingItem.setBoughtAt("Grocery Store");

            DigitalStorage digitalStorage = new DigitalStorage();
            digitalStorage.setTitle("Storage");
            shoppingItem.setStorage(digitalStorage);

            List<Ingredient> ingredientList = Arrays.asList(new Ingredient().setTitle("Ingredient1"), new Ingredient().setTitle("Ingredient2"));
            shoppingItem.setIngredientList(ingredientList);

            List<ItemLabel> labels = Arrays.asList(new ItemLabel().setLabelValue("Label1").setLabelColour("ff0000"),
                new ItemLabel().setLabelValue("Label1").setLabelColour("00ff00"));
            shoppingItem.setLabels(labels);

            ShoppingList shoppingList = new ShoppingList();
            shoppingList.setName(shoppingListNames.get(i));
            shoppingItem.setShoppingList(shoppingList);

            LOGGER.debug("saving item {}", shoppingItem);
            shoppingItemRepository.save(shoppingItem);
        }
    }
}
