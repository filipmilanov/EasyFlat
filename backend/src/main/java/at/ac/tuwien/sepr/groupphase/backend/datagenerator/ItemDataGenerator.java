package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.IngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlwaysInStockDigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientRepository;
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
import java.util.List;

@Profile({"generateData", "test"})
@Component("ItemDataGenerator")
@DependsOn({"CleanDatabase", "StorageDataGenerator", "IngredientsDataGenerator", "UnitDataGenerator"})
public class ItemDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ItemRepository itemRepository;
    private final UnitRepository unitRepository;
    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;

    public ItemDataGenerator(ItemRepository itemRepository,
                             UnitRepository unitRepository,
                             IngredientRepository ingredientRepository,
                             IngredientMapper ingredientMapper) {
        this.itemRepository = itemRepository;
        this.unitRepository = unitRepository;
        this.ingredientRepository = ingredientRepository;
        this.ingredientMapper = ingredientMapper;
    }

    @PostConstruct
    public void generateItems() {
        LOGGER.debug("generating Items");
        Unit pcs = unitRepository.findByName("pcs").orElseThrow();
        Unit g = unitRepository.findByName("g").orElseThrow();
        Unit ml = unitRepository.findByName("ml").orElseThrow();
        DigitalStorage storage = new DigitalStorage();
        storage.setStorageId(1L);

        DigitalStorageItem digitalStorageItem1 = generateDigitalStorageItem(
            "1234567890123",
            "apples",
            "Granny Smith Apples",
            "Granny Smith",
            1.0,
            1.0,
            pcs,
            LocalDate.now().plusDays(1),
            "Granny Smith Apples",
            "Billa",
            storage,
            List.of("Apple")
        );

        DigitalStorageItem digitalStorageItem2 = generateDigitalStorageItem(
            "4311501468753",
            "Junge Erbsen",
            "Junge Erbsen - Edeka Bio - 1pcs",
            "Edeka Bio",
            450.0,
            450.0,
            g,
            LocalDate.now().plusMonths(3),
            "",
            "Billa",
            storage,
            List.of("Erbsen")
        );

        DigitalStorageItem digitalStorageItem3 = generateDigitalStorageItem(
            "7622300441937",
            "Philadelphia",
            "Philadelphia Original - Kraft - 150g",
            "Kraft",
            150.0,
            150.0,
            g,
            LocalDate.now().plusMonths(3),
            "",
            "Billa",
            storage,
            List.of(
                "while milk",
                "cream",
                "milk protein preparation",
                "salt",
                "stabilizer (carob bean gum)"
            )
        );

        DigitalStorageItem digitalStorageItem4 = generateDigitalStorageAlwaysInStockItem(
            "8076809513722",
            "Sauce tomate au basilic",
            "Pesto alla Genovese - Barilla - 190g",
            "Barilla",
            190.0,
            190.0,
            g,
            LocalDate.now().plusMonths(3),
            "",
            "Billa",
            storage,
            List.of(
                "tomato pulp",
                "tomato concentrate",
                "sunflower seed oil",
                "basil",
                "salt",
                "sugar",
                "natural flavoring"
            ),
            100L
        );

        DigitalStorageItem digitalStorageItem5 = generateDigitalStorageItem(
            "8076809513753",
            "Sauce tomate au basilic",
            "Pesto alla Genovese - Barilla - 190g",
            "Barilla",
            190.0,
            190.0,
            g,
            LocalDate.now().plusMonths(3),
            "",
            "Billa",
            storage,
            List.of(
                "sunflower seed oil",
                "basil",
                "cashew nuts",
                "grana padano dop cheese",
                "salt",
                "pecorino romano dop cheese",
                "sugar"
            )
        );

        DigitalStorageItem digitalStorageItem6 = generateDigitalStorageAlwaysInStockItem(
            "8076800195057",
            "Noodles",
            "Spaghetti - Barilla - 500g",
            "Barilla",
            450.0,
            500.0,
            g,
            LocalDate.now().plusMonths(3),
            "",
            "Billa",
            storage,
            List.of(
                "durum wheat semolina",
                "water"
            ),
            150L
        );

        DigitalStorageItem digitalStorageItem7 = generateDigitalStorageAlwaysInStockItem(
            "8076802085738",
            "Noodles",
            "Nudeln Penne Rigate N°73 - Barilla - 500g",
            "Barilla",
            500.0,
            500.0,
            g,
            LocalDate.now().plusMonths(3),
            "",
            "Billa",
            storage,
            List.of(
                "wheat semolina",
                "water"
            ),
            150L
        );

        DigitalStorageItem digitalStorageItem8 = generateDigitalStorageItem(
            null,
            "Milk",
            "Milch 3,5% Fett - Spar - 1l",
            "Spar",
            200.0,
            1000.0,
            ml,
            LocalDate.now().plusMonths(3),
            "",
            "Billa",
            storage,
            List.of(
                "milk"
            )
        );

        DigitalStorageItem digitalStorageItem9 = generateDigitalStorageItem(
            null,
            "Milk",
            "Almonddrink - Alpro - 1l",
            "alpro",
            1000.0,
            1000.0,
            ml,
            LocalDate.now().plusMonths(3),
            "",
            "Spar",
            storage,
            List.of(
                "water",
                "almond",
                "sugar",
                "calcium",
                "sea salt",
                "stabilizer (locust bean gum, gellan gum)",
                "emulsifier (sunflower lecithin)",
                "vitamins (riboflavin (B2), B12, E, D2)"
            )
        );

        DigitalStorageItem digitalStorageItem10 = generateDigitalStorageItem(
            "5411188103387",
            "Yogurt",
            "Alpro Vanille - 500 g",
            "alpro",
            250.0,
            500.0,
            g,
            LocalDate.now().plusMonths(3),
            "",
            "Spar",
            storage,
            List.of(
                "water",
                "hulled soya beans (7.9%)",
                "sugar",
                "glucose-fructose syrup",
                "vanilla (0.5%)",
                "tricalcium phosphate",
                "stabilizer (pectin)",
                "sea salt",
                "natural flavouring",
                "acidity regulator (citric acid)",
                "antioxidants (tocopherol-rich extract, fatty acid esters of ascorbic acid)",
                "vitamins (riboflavin (B2), B12, D2)"
            )
        );

        DigitalStorageItem digitalStorageItem11 = generateDigitalStorageItem(
            "8000500037560",
            "Sweets",
            "Kinder Schokolade - 100 g",
            "Ferrero",
            100.0,
            100.0,
            g,
            LocalDate.now().plusMonths(3),
            "",
            "Spar",
            storage,
            List.of(
                "fine milk chocolate 40% (sugar, whole milk powder, cocoa butter, cocoa mass, emulsifier lecithin (soy), vanillin)",
                "sugar",
                "skimmed milk powder",
                "vegetable fats (palm, shea)",
                "concentrated butter",
                "emulsifier lecithin (soy)",
                "vanillin"
            )
        );


        LOGGER.debug("saving digitalStorageItem {}", digitalStorageItem1);
        itemRepository.saveAll(List.of(
                digitalStorageItem1,
                digitalStorageItem2,
                digitalStorageItem3,
                digitalStorageItem4,
                digitalStorageItem5,
                digitalStorageItem6,
            digitalStorageItem7,
            digitalStorageItem8,
            digitalStorageItem9,
            digitalStorageItem10,
            digitalStorageItem11
            )
        );

    }

    private DigitalStorageItem generateDigitalStorageItem(String ean,
                                                          String generalName,
                                                          String productName,
                                                          String brand,
                                                          Double quantityCurrent,
                                                          Double quantityTotal,
                                                          Unit unit,
                                                          LocalDate expireDate,
                                                          String description,
                                                          String boughtAt,
                                                          DigitalStorage digitalStorage,
                                                          List<String> ingredientList) {
        DigitalStorageItem digitalStorageItem = new DigitalStorageItem();
        digitalStorageItem.getItemCache().setEan(ean);
        digitalStorageItem.getItemCache().setGeneralName(generalName);
        digitalStorageItem.getItemCache().setProductName(productName);
        digitalStorageItem.getItemCache().setBrand(brand);
        digitalStorageItem.setQuantityCurrent(quantityCurrent);
        digitalStorageItem.getItemCache().setQuantityTotal(quantityTotal);
        digitalStorageItem.getItemCache().setUnit(unit);
        digitalStorageItem.setExpireDate(expireDate);
        digitalStorageItem.getItemCache().setDescription(description);
        digitalStorageItem.setBoughtAt(boughtAt);
        digitalStorageItem.setDigitalStorage(digitalStorage);
        digitalStorageItem.setIngredientList(findIngredientsAndCreateMissing(
            ingredientList.stream()
                .map(ingredientName -> new IngredientDto(null, ingredientName))
                .toList()
        ));
        return digitalStorageItem;
    }

    private DigitalStorageItem generateDigitalStorageAlwaysInStockItem(String ean,
                                                                       String generalName,
                                                                       String productName,
                                                                       String brand,
                                                                       Double quantityCurrent,
                                                                       Double quantityTotal,
                                                                       Unit unit,
                                                                       LocalDate expireDate,
                                                                       String description,
                                                                       String boughtAt,
                                                                       DigitalStorage digitalStorage,
                                                                       List<String> ingredientList,
                                                                       Long minimumQuantity) {
        DigitalStorageItem digitalStorageItem = new AlwaysInStockDigitalStorageItem();
        digitalStorageItem.getItemCache().setEan(ean);
        digitalStorageItem.getItemCache().setGeneralName(generalName);
        digitalStorageItem.getItemCache().setProductName(productName);
        digitalStorageItem.getItemCache().setBrand(brand);
        digitalStorageItem.setQuantityCurrent(quantityCurrent);
        digitalStorageItem.getItemCache().setQuantityTotal(quantityTotal);
        digitalStorageItem.getItemCache().setUnit(unit);
        digitalStorageItem.setExpireDate(expireDate);
        digitalStorageItem.getItemCache().setDescription(description);
        digitalStorageItem.setBoughtAt(boughtAt);
        digitalStorageItem.setDigitalStorage(digitalStorage);
        digitalStorageItem.setIngredientList(findIngredientsAndCreateMissing(
            ingredientList.stream()
                .map(ingredientName -> new IngredientDto(null, ingredientName))
                .toList()
        ));
        digitalStorageItem.setMinimumQuantity(minimumQuantity);

        return digitalStorageItem;
    }

    public List<Ingredient> findIngredientsAndCreateMissing(List<IngredientDto> ingredientDtoList) {
        if (ingredientDtoList == null) {
            return List.of();
        }
        List<Ingredient> ingredientList = ingredientRepository.findAllByTitleIsIn(
            ingredientDtoList.stream()
                .map(IngredientDto::name)
                .toList()
        );

        List<IngredientDto> missingIngredients = ingredientDtoList.stream()
            .filter(ingredientDto ->
                ingredientList.stream()
                    .noneMatch(ingredient ->
                        ingredient.getTitle().equals(ingredientDto.name())
                    )
            ).toList();

        if (!missingIngredients.isEmpty()) {
            List<Ingredient> createdIngredients = ingredientRepository.saveAll(
                ingredientMapper.dtoListToEntityList(missingIngredients)
            );
            ingredientList.addAll(createdIngredients);
        }
        return ingredientList;
    }
}
