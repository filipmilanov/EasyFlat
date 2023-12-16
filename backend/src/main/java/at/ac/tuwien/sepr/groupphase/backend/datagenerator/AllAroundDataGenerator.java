package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cookbook;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeSuggestion;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.CookbookRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.DigitalStorageRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeSuggestionRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SharedFlatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UnitRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.SharedFlatService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

@Profile({"presentationData"})
@Component("AllAroundDataGenerator")
@DependsOn({"CleanDatabase", "UnitDataGenerator"})
public class AllAroundDataGenerator {
    private final PasswordEncoder passwordEncoder;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final SharedFlatRepository sharedFlatRepository;
    private final UnitRepository unitRepository;
    private final ItemRepository itemRepository;
    private final DigitalStorageRepository digitalStorageRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingItemRepository shoppingItemRepository;
    private final CookbookRepository cookbookRepository;
    private final RecipeSuggestionRepository recipeSuggestionRepository;
    private final IngredientRepository ingredientRepository;

    public AllAroundDataGenerator(PasswordEncoder passwordEncoder,
                                  UserRepository userRepository,
                                  SharedFlatRepository sharedFlatRepository,
                                  UnitRepository unitRepository,
                                  ItemRepository itemRepository,
                                  DigitalStorageRepository digitalStorageRepository,
                                  ShoppingListRepository shoppingListRepository,
                                  ShoppingItemRepository shoppingItemRepository,
                                  CookbookRepository cookbookRepository,
                                  RecipeSuggestionRepository recipeSuggestionRepository,
                                  IngredientRepository ingredientRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.sharedFlatRepository = sharedFlatRepository;
        this.unitRepository = unitRepository;
        this.itemRepository = itemRepository;
        this.digitalStorageRepository = digitalStorageRepository;
        this.shoppingListRepository = shoppingListRepository;
        this.shoppingItemRepository = shoppingItemRepository;
        this.cookbookRepository = cookbookRepository;
        this.recipeSuggestionRepository = recipeSuggestionRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @PostConstruct
    public void initData() {
        this.generateSharedFlats();
        this.generateApplicationUsers();
        this.generateItems();
        this.generateShoppingItems();
        this.generateCookbook();
        this.generateRezipe();
    }


    public void generateSharedFlats() {
        SharedFlat sharedFlat1 = new SharedFlat();
        sharedFlat1.setName("Stephansplatz Luxus WG");
        sharedFlat1.setPassword(passwordEncoder.encode("12341234"));

        LOGGER.debug("saving shared flat: {}", sharedFlat1);
        sharedFlatRepository.save(sharedFlat1);
        DigitalStorage digitalStorage1 = new DigitalStorage();
        digitalStorage1.setTitle("Storage");
        digitalStorage1.setSharedFlat(sharedFlat1);
        digitalStorageRepository.save(digitalStorage1);

        SharedFlat sharedFlat2 = new SharedFlat();
        sharedFlat2.setName("Mariahilfer Straße Studentenheim");
        sharedFlat2.setPassword(passwordEncoder.encode("43214321"));

        LOGGER.debug("saving shared flat: {}", sharedFlat2);
        sharedFlatRepository.save(sharedFlat2);
        DigitalStorage digitalStorage2 = new DigitalStorage();
        digitalStorage2.setTitle("Storage");
        digitalStorage2.setSharedFlat(sharedFlat2);
        digitalStorageRepository.save(digitalStorage2);
    }

    public void generateApplicationUsers() {
        ApplicationUser user1 = new ApplicationUser();
        user1.setFirstName("Martin");
        user1.setLastName("Johnson");
        user1.setEmail("user1@email.com");
        user1.setPassword(passwordEncoder.encode("password1"));
        user1.setAdmin(true);
        user1.setSharedFlat(sharedFlatRepository.findFirstByName("Stephansplatz Luxus WG"));
        LOGGER.debug("saving user: {}", user1);
        userRepository.save(user1);

        ApplicationUser user2 = new ApplicationUser();
        user2.setFirstName("Emily");
        user2.setLastName("Davis");
        user2.setEmail("user2@email.com");
        user2.setPassword(passwordEncoder.encode("password2"));
        user2.setAdmin(false);
        user2.setSharedFlat(sharedFlatRepository.findFirstByName("Stephansplatz Luxus WG"));
        LOGGER.debug("saving user: {}", user2);
        userRepository.save(user2);

        ApplicationUser user3 = new ApplicationUser();
        user3.setFirstName("Alex");
        user3.setLastName("Smith");
        user3.setEmail("user3@email.com");
        user3.setPassword(passwordEncoder.encode("password3"));
        user3.setAdmin(false);
        user3.setSharedFlat(sharedFlatRepository.findFirstByName("Stephansplatz Luxus WG"));
        LOGGER.debug("saving user: {}", user3);
        userRepository.save(user3);

        ApplicationUser user4 = new ApplicationUser();
        user4.setFirstName("Sophia");
        user4.setLastName("Miller");
        user4.setEmail("user4@email.com");
        user4.setPassword(passwordEncoder.encode("password4"));
        user4.setAdmin(true);
        user4.setSharedFlat(sharedFlatRepository.findFirstByName("Mariahilfer Straße Studentenwg"));
        LOGGER.debug("saving user: {}", user4);
        userRepository.save(user4);

        ApplicationUser user5 = new ApplicationUser();
        user5.setFirstName("Daniel");
        user5.setLastName("Brown");
        user5.setEmail("user5@email.com");
        user5.setPassword(passwordEncoder.encode("password5"));
        user5.setAdmin(false);
        user5.setSharedFlat(sharedFlatRepository.findFirstByName("Mariahilfer Straße Studentenwg"));
        LOGGER.debug("saving user: {}", user5);
        userRepository.save(user5);

        ApplicationUser user6 = new ApplicationUser();
        user6.setFirstName("Olivia");
        user6.setLastName("Wilson");
        user6.setEmail("user6@email.com");
        user6.setPassword(passwordEncoder.encode("password6"));
        user6.setAdmin(false);
        user6.setSharedFlat(sharedFlatRepository.findFirstByName("Mariahilfer Straße Studentenheim"));
        LOGGER.debug("saving user: {}", user6);
        userRepository.save(user6);

    }

    public void generateItems() {
        DigitalStorage storage1 = digitalStorageRepository.getReferenceById(1L);

        Unit l = unitRepository.findByName("l").orElseThrow();
        Item item1 = new Item();
        item1.setGeneralName("milk");
        item1.setEan("1234567890124");
        item1.setProductName("organic whole milk");
        item1.setBrand("Organic Farms");
        item1.setQuantityCurrent(1.5);
        item1.setQuantityTotal(3.0);
        item1.setUnit(l);
        item1.setExpireDate(LocalDate.now().plusDays(7));
        item1.setDescription("Fresh and organic whole milk for a wholesome taste.");
        item1.setPriceInCent(250L);
        item1.setBoughtAt("Local Dairy");
        item1.setStorage(storage1);
        item1.setMinimumQuantity(1L);
        Ingredient ingredient1 = new Ingredient();
        ingredient1.setTitle("Vitamin D");
        ingredient1 = ingredientRepository.save(ingredient1);
        Ingredient ingredient2 = new Ingredient();
        ingredient2.setTitle("Calcium");
        ingredient2 = ingredientRepository.save(ingredient2);
        item1.setIngredientList(Arrays.asList(ingredient1, ingredient2));
        LOGGER.debug("saving item {}", item1);
        itemRepository.save(item1);

        Unit g = unitRepository.findByName("g").orElseThrow();
        Item item2 = new Item();
        item2.setGeneralName("bread");
        item2.setEan("1234567890123");
        item2.setProductName("whole-grain bread");
        item2.setBrand("Bakery Delights");
        item2.setQuantityCurrent(500.0);
        item2.setQuantityTotal(1000.0);
        item2.setUnit(g);
        item2.setExpireDate(LocalDate.now().plusDays(5));
        item2.setDescription("Healthy whole-grain bread for a nutritious diet.");
        item2.setPriceInCent(650L);
        item2.setBoughtAt("Bakery");
        item2.setStorage(storage1);
        item2.setMinimumQuantity(300L);
        Ingredient ingredient3 = new Ingredient();
        ingredient3.setTitle("Whole Wheat Flour");
        ingredient3 = ingredientRepository.save(ingredient3);
        Ingredient ingredient4 = new Ingredient();
        ingredient4.setTitle("Yeast");
        ingredient4 = ingredientRepository.save(ingredient4);
        item2.setIngredientList(Arrays.asList(ingredient3, ingredient4));
        LOGGER.debug("saving item {}", item2);
        itemRepository.save(item2);

        Unit kg = unitRepository.findByName("kg").orElseThrow();
        Item item3 = new Item();
        item3.setGeneralName("chicken");
        item3.setEan("1234567890124");
        item3.setProductName("organic chicken breasts");
        item3.setBrand("Farm Fresh");
        item3.setQuantityCurrent(2.0);
        item3.setQuantityTotal(5.0);
        item3.setUnit(kg);
        item3.setExpireDate(LocalDate.now().plusDays(14));
        item3.setDescription("High-quality organic chicken breasts for a delicious meal.");
        item3.setPriceInCent(1200L);
        item3.setBoughtAt("Butcher Shop");
        item3.setStorage(storage1);
        LOGGER.debug("saving item {}", item3);
        itemRepository.save(item3);

        Unit ml = unitRepository.findByName("ml").orElseThrow();
        Item item4 = new Item();
        item4.setGeneralName("coffee");
        item4.setEan("1234567890125");
        item4.setProductName("arabica coffee beans");
        item4.setBrand("Artisan Roasters");
        item4.setQuantityCurrent(500.0);
        item4.setQuantityTotal(1000.0);
        item4.setUnit(ml);
        item4.setExpireDate(null);
        item4.setDescription("Premium arabica coffee beans for the perfect cup of coffee.");
        item4.setPriceInCent(1500L);
        item4.setBoughtAt("Coffee Shop");
        item4.setStorage(storage1);
        Ingredient ingredient6 = new Ingredient();
        ingredient6.setTitle("Vanilla Syrup");
        ingredient6 = ingredientRepository.save(ingredient6);
        item4.setIngredientList(Collections.singletonList(ingredient6));
        LOGGER.debug("saving item {}", item4);
        itemRepository.save(item4);

        DigitalStorage storage2 = digitalStorageRepository.getReferenceById(2L);

        Item item5 = new Item();
        item5.setGeneralName("tomatoes");
        item5.setEan("1234567890126");
        item5.setProductName("plum tomatoes");
        item5.setBrand("Organic Farms");
        item5.setQuantityCurrent(1.5);
        item5.setQuantityTotal(3.0);
        item5.setUnit(kg);
        item5.setExpireDate(LocalDate.now().plusDays(7));
        item5.setDescription("Fresh and juicy plum tomatoes for salads and sauces.");
        item5.setPriceInCent(800L);
        item5.setBoughtAt("Farmers Market");
        item5.setStorage(storage2);
        item5.setMinimumQuantity(1L);
        LOGGER.debug("saving item {}", item5);
        itemRepository.save(item5);

        Unit servings = unitRepository.findByName("servings").orElseThrow();
        Item item6 = new Item();
        item6.setGeneralName("pasta");
        item6.setEan("1234567890127");
        item6.setProductName("whole wheat pasta");
        item6.setBrand("Healthy Harvest");
        item6.setQuantityCurrent(3.0);
        item6.setQuantityTotal(6.0);
        item6.setUnit(servings);
        item6.setExpireDate(LocalDate.now().plusDays(2));
        item6.setDescription("Nutritious whole wheat pasta for a wholesome meal.");
        item6.setPriceInCent(450L);
        item6.setBoughtAt("Grocery Store");
        item6.setStorage(storage2);
        Ingredient ingredient9 = new Ingredient();
        ingredient9.setTitle("Tomato Sauce");
        Ingredient ingredient10 = new Ingredient();
        ingredient10.setTitle("Parmesan Cheese");
        ingredient9 = ingredientRepository.save(ingredient9);
        ingredient10 = ingredientRepository.save(ingredient10);
        item6.setIngredientList(Arrays.asList(ingredient9, ingredient10));
        LOGGER.debug("saving item {}", item6);
        itemRepository.save(item6);
    }

    public void generateShoppingItems() {
        ShoppingList shoppingList1 = new ShoppingList();
        shoppingList1.setName("Groceries");
        shoppingList1.setSharedFlat(sharedFlatRepository.findFirstByName("Stephansplatz Luxus WG"));

        ShoppingList shoppingList2 = new ShoppingList();
        shoppingList2.setName("Home Improvements");
        shoppingList2.setSharedFlat(sharedFlatRepository.findFirstByName("Stephansplatz Luxus WG"));


        ShoppingList shoppingList3 = new ShoppingList();
        shoppingList3.setName("Foods");
        shoppingList3.setSharedFlat(sharedFlatRepository.findFirstByName("Mariahilfer Straße Studentenheim"));


        ShoppingList shoppingList4 = new ShoppingList();
        shoppingList4.setName("Billa");
        shoppingList4.setSharedFlat(sharedFlatRepository.findFirstByName("Mariahilfer Straße Studentenheim"));

        Unit g = unitRepository.findByName("g").orElseThrow();
        ShoppingItem shoppingItem1 = new ShoppingItem();
        shoppingItem1.setProductName("Mehl");
        shoppingItem1.setUnit(g);
        shoppingItem1.setShoppingList(shoppingListRepository.save(shoppingList1));
        shoppingItem1.setQuantityCurrent(1000L);

        ShoppingItem shoppingItem2 = new ShoppingItem();
        shoppingItem2.setProductName("Schokolade");
        shoppingItem2.setUnit(g);
        shoppingItem2.setQuantityCurrent(100L);
        shoppingItem2.setShoppingList(shoppingListRepository.save(shoppingList2));

        Unit l = unitRepository.findByName("l").orElseThrow();
        ShoppingItem shoppingItem3 = new ShoppingItem();
        shoppingItem3.setProductName("Milch");
        shoppingItem3.setUnit(l);
        shoppingItem3.setQuantityCurrent(4L);
        shoppingItem3.setShoppingList(shoppingListRepository.save(shoppingList3));
        ShoppingItem shoppingItem4 = new ShoppingItem();
        shoppingItem4.setProductName("Weintrauben");
        shoppingItem4.setUnit(g);
        shoppingItem4.setQuantityCurrent(4L);
        shoppingItem4.setShoppingList(shoppingListRepository.save(shoppingList4));

        shoppingItemRepository.save(shoppingItem1);
        shoppingItemRepository.save(shoppingItem2);
        shoppingItemRepository.save(shoppingItem3);
        shoppingItemRepository.save(shoppingItem4);
    }

    public void generateRezipe() {
        Cookbook cookbook = cookbookRepository.findById(1L).orElseThrow();

        RecipeSuggestion rescipe = new RecipeSuggestion();
        rescipe.setTitle("Amerikanische Double Choc Brownies");
        rescipe.setSummary("Preheat the oven to 200°C.\n"
            + "Roll out the pizza dough. Spread the ingredients evenly over it, leaving a 1 cm wide edge. Roll up from the wide side. Cut the pizza roll into 3 cm wide slices with a sharp knife.\n"
            + "Place on the prepared baking tray and bake for approx. 20 mins. ");
        rescipe.setReadyInMinutes(30);
        rescipe.setCookbook(cookbook);

        RecipeSuggestion rescipe2 = new RecipeSuggestion();
        rescipe2.setTitle("Pizzarolles");
        rescipe2.setSummary("Melt 200 g of the dark chocolate with 120 g of butter, stir and leave to cool slightly. Chop up the remaining chocolate (it is best to crumble it in a freezer bag). Mix the flour with the baking powder and "
            + "salt. Beat the eggs, sugar and vanilla sugar until frothy and add the lukewarm chocolate mixture. Gradually sift in the flour mixture and carefully mix everything into a dough. Now fold in the remaining chocolate chips. "
            + "Alternatively, you can also use a handful of chopped walnuts.\nGrease a brownies baking tin (approx. 23 x 23 cm) with the remaining butter, pour in the batter and smooth out. "
            + "Then place in the oven preheated to 180 degrees with a rack on the middle shelf for 20 - 25 minutes. You need to find the right time here so that the brownies are still nice and moist and crispy on the inside. "
            + "It is best to leave them in for 20 minutes or more. They should be ready when the edges are a nice deep dark brown.\n"
            + "\nAfter cooling, either leave them plain or spread with milk chocolate coating and decorate with diagonal stripes of white chocolate coating. Cut into 16 squares or 32 smaller rectangles.\n"
            + "\nVery suitable for freezing. Once frozen, allow to thaw briefly and serve with a scoop of vanilla ice cream. ");
        rescipe2.setReadyInMinutes(30);
        rescipe2.setCookbook(cookbook);

        recipeSuggestionRepository.save(rescipe);
        recipeSuggestionRepository.save(rescipe2);
    }


    public void generateCookbook() {
        SharedFlat sharedFlat1 = sharedFlatRepository.findById(1L).orElseThrow();

        Cookbook cookbook1 = new Cookbook();
        cookbook1.setTitle("My Cookbook");
        cookbook1.setSharedFlat(sharedFlat1);

        SharedFlat sharedFlat2 = sharedFlatRepository.findById(2L).orElseThrow();
        Cookbook cookbook2 = new Cookbook();
        cookbook2.setTitle("My Cookbook");
        cookbook2.setSharedFlat(sharedFlat2);

        cookbookRepository.save(cookbook1);
        cookbookRepository.save(cookbook2);
    }


}
