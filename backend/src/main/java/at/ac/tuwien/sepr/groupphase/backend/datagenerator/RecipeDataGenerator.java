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
    private static final int NUMBER_OF_ENTITIES_TO_GENERATE = 5;
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
        Unit unit = unitRepository.findByName("kg").orElseThrow();

        for (int i = 0; i < NUMBER_OF_ENTITIES_TO_GENERATE; i++) {
            RecipeSuggestion recipe = new RecipeSuggestion();
            recipe.setTitle("Recipe Number " + (i + 1));
            recipe.setSummary("This is recipe " + (i + 1));
            recipe.setServings(2);
            recipe.setReadyInMinutes(20);
            recipe.setCookbook(cookbook);

            RecipeIngredient ingredient1 = new RecipeIngredient();
            ingredient1.setName("Banana " +  (i * 2 + 1));
            ingredient1.setAmount(1);
            ingredient1.setUnit(unit.getName());
            ingredient1.setUnitEnum(unit);

            RecipeIngredient ingredient2 = new RecipeIngredient();
            ingredient2.setName("Apple " +  (i * 2 + 2));
            ingredient2.setAmount(1);
            ingredient2.setUnit(unit.getName());
            ingredient2.setUnitEnum(unit);

            List<RecipeIngredient> ingredients = new ArrayList<>();
            ingredients.addAll(List.of(ingredient1, ingredient2));
            recipeIngredientRepository.saveAll(ingredients);

            recipe.setExtendedIngredients(ingredients);
            recipeSuggestionRepository.save(recipe);

        }
    }
}
