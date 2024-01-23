package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Cookbook;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeIngredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeSuggestion;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeIngredientRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeSuggestionRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UnitRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Profile({"generateData", "test"})
@Component("RecipeDataGenerator")
@DependsOn({"CleanDatabase", "CookbookDataGenerator", "UnitDataGenerator"})
public class RecipeDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final RecipeSuggestionRepository recipeSuggestionRepository;
    private final UnitRepository unitRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;


    public RecipeDataGenerator(RecipeSuggestionRepository recipeSuggestionRepository,
                               UnitRepository unitRepository,
                               RecipeIngredientRepository recipeIngredientRepository) {
        this.recipeSuggestionRepository = recipeSuggestionRepository;
        this.unitRepository = unitRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
    }

    @PostConstruct
    public void generateItems() {
        LOGGER.debug("generating Items");
        Cookbook cookbook = new Cookbook();
        cookbook.setId(1L);
        Unit kg = unitRepository.findByName("kg").orElseThrow();
        Unit g = unitRepository.findByName("g").orElseThrow();
        Unit l = unitRepository.findByName("l").orElseThrow();
        Unit ml = unitRepository.findByName("ml").orElseThrow();
        Unit pcs = unitRepository.findByName("pcs").orElseThrow();

        RecipeSuggestion recipe1 = new RecipeSuggestion();
        recipe1.setTitle("Spaghetti Bolognese");
        recipe1.setSummary("Classic Italian pasta dish with savory meat sauce. Begin by browning 500g of ground beef in a pan. "
            + "Add 400ml of tomato sauce and let it simmer, allowing the flavors to meld. Meanwhile, cook spaghetti until al dente. "
            + "Serve the rich meat sauce over the perfectly cooked spaghetti.");
        recipe1.setServings(4);
        recipe1.setReadyInMinutes(30);
        recipe1.setCookbook(cookbook);

        RecipeIngredient ingredient1_1 = new RecipeIngredient();
        ingredient1_1.setName("Ground Beef");
        ingredient1_1.setAmount(500);
        ingredient1_1.setUnit(g.getName());
        ingredient1_1.setUnitEnum(g);

        RecipeIngredient ingredient1_2 = new RecipeIngredient();
        ingredient1_2.setName("Tomato Sauce");
        ingredient1_2.setAmount(400);
        ingredient1_2.setUnit(ml.getName());
        ingredient1_2.setUnitEnum(ml);

        List<RecipeIngredient> ingredients1 = List.of(ingredient1_1, ingredient1_2);
        recipeIngredientRepository.saveAll(ingredients1);
        recipe1.setExtendedIngredients(ingredients1);
        recipeSuggestionRepository.save(recipe1);

        RecipeSuggestion recipe2 = new RecipeSuggestion();
        recipe2.setTitle("Easy Pasta Carbonara");
        recipe2.setSummary("A quick and creamy pasta dish with bacon and Parmesan cheese. Cook 200g of spaghetti until al dente. "
            + "In a separate bowl, whisk together 2 eggs and 50g of Parmesan cheese. In a pan, cook 100g of bacon until crispy. "
            + "Toss the cooked spaghetti with the egg and cheese mixture, adding the bacon. Serve immediately for a delightful carbonara.");
        recipe2.setServings(2);
        recipe2.setReadyInMinutes(20);
        recipe2.setCookbook(cookbook);

        RecipeIngredient ingredient2_1 = new RecipeIngredient();
        ingredient2_1.setName("Spaghetti");
        ingredient2_1.setAmount(200);
        ingredient2_1.setUnit(g.getName());
        ingredient2_1.setUnitEnum(g);

        RecipeIngredient ingredient2_2 = new RecipeIngredient();
        ingredient2_2.setName("Bacon");
        ingredient2_2.setAmount(100);
        ingredient2_2.setUnit(g.getName());
        ingredient2_2.setUnitEnum(g);

        RecipeIngredient ingredient2_3 = new RecipeIngredient();
        ingredient2_3.setName("Eggs");
        ingredient2_3.setAmount(2);
        ingredient2_3.setUnit(pcs.getName());
        ingredient2_3.setUnitEnum(pcs);

        RecipeIngredient ingredient2_4 = new RecipeIngredient();
        ingredient2_4.setName("Parmesan Cheese");
        ingredient2_4.setAmount(50);
        ingredient2_4.setUnit(g.getName());
        ingredient2_4.setUnitEnum(g);

        List<RecipeIngredient> ingredients2 = List.of(ingredient2_1, ingredient2_2, ingredient2_3, ingredient2_4);
        recipeIngredientRepository.saveAll(ingredients2);
        recipe2.setExtendedIngredients(ingredients2);
        recipeSuggestionRepository.save(recipe2);

        RecipeSuggestion recipe3 = new RecipeSuggestion();
        recipe3.setTitle("Chicken and Vegetable Soup");
        recipe3.setSummary("A nourishing and easy-to-make soup with chicken and assorted vegetables. Start by cooking 300g of chicken breast "
            + "until fully cooked. Add 2 diced carrots, 2 diced potatoes, and 1 chopped onion to the pot. Let the ingredients simmer in "
            + "chicken broth for 30 minutes. The result is a hearty and flavorful chicken and vegetable soup.");
        recipe3.setServings(4);
        recipe3.setReadyInMinutes(30);
        recipe3.setCookbook(cookbook);

        RecipeIngredient ingredient3_1 = new RecipeIngredient();
        ingredient3_1.setName("Chicken Breast");
        ingredient3_1.setAmount(300);
        ingredient3_1.setUnit(g.getName());
        ingredient3_1.setUnitEnum(g);

        RecipeIngredient ingredient3_2 = new RecipeIngredient();
        ingredient3_2.setName("Carrots");
        ingredient3_2.setAmount(2);
        ingredient3_2.setUnit(pcs.getName());
        ingredient3_2.setUnitEnum(pcs);

        RecipeIngredient ingredient3_3 = new RecipeIngredient();
        ingredient3_3.setName("Potatoes");
        ingredient3_3.setAmount(2);
        ingredient3_3.setUnit(pcs.getName());
        ingredient3_3.setUnitEnum(pcs);

        RecipeIngredient ingredient3_4 = new RecipeIngredient();
        ingredient3_4.setName("Onion");
        ingredient3_4.setAmount(1);
        ingredient3_4.setUnit(pcs.getName());
        ingredient3_4.setUnitEnum(pcs);

        List<RecipeIngredient> ingredients3 = List.of(ingredient3_1, ingredient3_2, ingredient3_3, ingredient3_4);
        recipeIngredientRepository.saveAll(ingredients3);
        recipe3.setExtendedIngredients(ingredients3);
        recipeSuggestionRepository.save(recipe3);


        RecipeSuggestion recipe4 = new RecipeSuggestion();
        recipe4.setTitle("Grandma's Breakfast Pancakes");
        recipe4.setSummary("Delicious and fluffy pancakes made with love, just like grandma used to make. In a bowl, mix 200g of flour, "
            + "250ml of milk, 2 eggs, and 25g of sugar. Heat a griddle or pan and pour the pancake batter. Cook until bubbles form on "
            + "the surface, then flip and cook until golden brown. Serve these nostalgic pancakes with your favorite toppings.");
        recipe4.setServings(2);
        recipe4.setReadyInMinutes(20);
        recipe4.setCookbook(cookbook);

        RecipeIngredient ingredient4_1 = new RecipeIngredient();
        ingredient4_1.setName("Flour");
        ingredient4_1.setAmount(200);
        ingredient4_1.setUnit(g.getName());
        ingredient4_1.setUnitEnum(g);

        RecipeIngredient ingredient4_2 = new RecipeIngredient();
        ingredient4_2.setName("Milk");
        ingredient4_2.setAmount(250);
        ingredient4_2.setUnit(ml.getName());
        ingredient4_2.setUnitEnum(ml);

        RecipeIngredient ingredient4_3 = new RecipeIngredient();
        ingredient4_3.setName("Eggs");
        ingredient4_3.setAmount(2);
        ingredient4_3.setUnit(pcs.getName());
        ingredient4_3.setUnitEnum(pcs);

        RecipeIngredient ingredient4_4 = new RecipeIngredient();
        ingredient4_4.setName("Sugar");
        ingredient4_4.setAmount(25);
        ingredient4_4.setUnit(g.getName());
        ingredient4_4.setUnitEnum(g);


        List<RecipeIngredient> ingredients4 = List.of(ingredient4_1, ingredient4_2, ingredient4_3, ingredient4_4);
        recipeIngredientRepository.saveAll(ingredients4);
        recipe4.setExtendedIngredients(ingredients4);
        recipeSuggestionRepository.save(recipe4);


        RecipeSuggestion recipe5 = new RecipeSuggestion();
        recipe5.setTitle("Homemade Vegetable Soup");
        recipe5.setSummary("Hearty and nutritious vegetable soup made from scratch. Begin by sautéing 150g of onions and 2 cloves of garlic "
            + "in a pot. Add 250g of carrots, 300g of potatoes, and 1 liter of vegetable broth. Let the soup simmer for 40 minutes, "
            + "resulting in a comforting and flavorful homemade vegetable soup.");
        recipe5.setServings(6);
        recipe5.setReadyInMinutes(40);
        recipe5.setCookbook(cookbook);

        RecipeIngredient ingredient5_1 = new RecipeIngredient();
        ingredient5_1.setName("Carrots");
        ingredient5_1.setAmount(250);
        ingredient5_1.setUnit(g.getName());
        ingredient5_1.setUnitEnum(g);

        RecipeIngredient ingredient5_2 = new RecipeIngredient();
        ingredient5_2.setName("Potatoes");
        ingredient5_2.setAmount(300);
        ingredient5_2.setUnit(g.getName());
        ingredient5_2.setUnitEnum(g);

        RecipeIngredient ingredient5_3 = new RecipeIngredient();
        ingredient5_3.setName("Onions");
        ingredient5_3.setAmount(150);
        ingredient5_3.setUnit(g.getName());
        ingredient5_3.setUnitEnum(g);

        RecipeIngredient ingredient5_4 = new RecipeIngredient();
        ingredient5_4.setName("Garlic");
        ingredient5_4.setAmount(2);
        ingredient5_4.setUnit(pcs.getName());
        ingredient5_4.setUnitEnum(pcs);

        RecipeIngredient ingredient5_5 = new RecipeIngredient();
        ingredient5_5.setName("Vegetable Broth");
        ingredient5_5.setAmount(1);
        ingredient5_5.setUnit(l.getName());
        ingredient5_5.setUnitEnum(l);

        List<RecipeIngredient> ingredients5 = List.of(ingredient5_1, ingredient5_2, ingredient5_3, ingredient5_4, ingredient5_5);
        recipeIngredientRepository.saveAll(ingredients5);
        recipe5.setExtendedIngredients(ingredients5);
        recipeSuggestionRepository.save(recipe5);

    }
}
