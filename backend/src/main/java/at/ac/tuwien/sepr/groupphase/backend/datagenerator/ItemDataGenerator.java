package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Profile({"generateData", "test"})
@Component("ItemDataGenerator")
@DependsOn({"CleanDatabase", "StorageDataGenerator", "IngredientsDataGenerator"})
public class ItemDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ENTITIES_TO_GENERATE = 5;
    private final ItemRepository itemRepository;

    public ItemDataGenerator(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @PostConstruct
    public void generateItems() {
        LOGGER.debug("generating {} Items ", NUMBER_OF_ENTITIES_TO_GENERATE);
        for (int i = 0; i < NUMBER_OF_ENTITIES_TO_GENERATE; i++) {
            Item item = new Item();
            item.setGeneralName("Item" + (i + 1));
            item.setEan("123456789012" + i);  // Replace with valid EAN numbers
            item.setProductName("Test Product " + (i + 1));
            item.setBrand("Test Brand " + (i + 1));
            item.setQuantityCurrent(10L + i);
            item.setQuantityTotal(20L + i);
            item.setUnit("pieces");
            item.setExpireDate(LocalDate.now().plusMonths(i + 1));  // Set expire date to current date + i months
            item.setDescription("This is a test product description for Item " + (i + 1));
            item.setPriceInCent(500L + i * 100);

            DigitalStorage storage = new DigitalStorage();
            storage.setStorId(1L);
            item.setStorage(storage);

            List<Ingredient> ingredientList = new ArrayList<>();
            Ingredient ingredient1 = new Ingredient();
            ingredient1.setIngrId(1L);
            Ingredient ingredient2 = new Ingredient();
            ingredient2.setIngrId(2L);
            ingredientList.add(ingredient1);
            ingredientList.add(ingredient2);
            item.setIngredientList(ingredientList);

            LOGGER.debug("saving item {}", item);
            itemRepository.save(item);
        }
    }
}
