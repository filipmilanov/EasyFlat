package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UnitRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Profile({"generateData", "test"})
@Component("ItemDataGenerator")
@DependsOn({"CleanDatabase", "StorageDataGenerator", "IngredientsDataGenerator", "UnitDataGenerator"})
public class ItemDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ENTITIES_TO_GENERATE = 5;
    private final ItemRepository itemRepository;
    private final UnitRepository unitRepository;

    public ItemDataGenerator(ItemRepository itemRepository,
                             UnitRepository unitRepository) {
        this.itemRepository = itemRepository;
        this.unitRepository = unitRepository;
    }

    @PostConstruct
    public void generateDigitalStorages() {
        LOGGER.debug("generating {} Items ", NUMBER_OF_ENTITIES_TO_GENERATE);
        Unit kg = unitRepository.findByName("kg").orElseThrow();
        List<String> generalNames = getGeneralNames();

        for (int i = 0; i < NUMBER_OF_ENTITIES_TO_GENERATE; i++) {

            DigitalStorageItem digitalStorageItem = new DigitalStorageItem();
            digitalStorageItem.setGeneralName(generalNames.get(i));
            digitalStorageItem.setEan("123456789012" + i);  // Replace with valid EAN numbers
            digitalStorageItem.setProductName("Test Product " + (i + 1));
            digitalStorageItem.setBrand("Test Brand " + (i + 1));
            digitalStorageItem.setQuantityCurrent(10.0 + i);
            digitalStorageItem.setQuantityTotal(20.0 + i);
            digitalStorageItem.setUnit(kg);
            digitalStorageItem.setExpireDate(LocalDate.now().plusMonths(i + 1));  // Set expire date to current date + i months
            digitalStorageItem.setDescription("This is a test product description for DigitalStorageItem " + (i + 1));
            digitalStorageItem.setPriceInCent(500L + i * 100);
            digitalStorageItem.setBoughtAt("Hofer");

            DigitalStorage storage = new DigitalStorage();
            storage.setStorId(1L);
            digitalStorageItem.setStorage(storage);

            List<Ingredient> ingredientList = new ArrayList<>();
            Ingredient ingredient1 = new Ingredient();
            ingredient1.setIngrId(1L);
            Ingredient ingredient2 = new Ingredient();
            ingredient2.setIngrId(2L);
            ingredientList.add(ingredient1);
            ingredientList.add(ingredient2);
            digitalStorageItem.setIngredientList(ingredientList);

            LOGGER.debug("saving digitalStorageItem {}", digitalStorageItem);
            itemRepository.save(digitalStorageItem);
        }
    }

    private List<String> getGeneralNames() {
        List<String> generalNames = new LinkedList<>();
        generalNames.add("apples");
        generalNames.add("flour");
        generalNames.add("sugar");
        generalNames.add("milk");
        generalNames.add("eggs");
        generalNames.add("cheese");
        generalNames.add("oranges");
        generalNames.add("chicken");
        generalNames.add("tomatoes");
        generalNames.add("onions");
        generalNames.add("rice");
        generalNames.add("pasta");
        generalNames.add("coffee");
        generalNames.add("tea");
        generalNames.add("bread");
        generalNames.add("butter");
        generalNames.add("salt");
        generalNames.add("pepper");
        generalNames.add("lettuce");
        generalNames.add("carrots");
        generalNames.add("potatoes");
        generalNames.add("broccoli");
        generalNames.add("cucumber");
        generalNames.add("strawberries");
        generalNames.add("blueberries");
        return generalNames;

    }
}
