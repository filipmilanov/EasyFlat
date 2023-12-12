package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.CookingSteps;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeIngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.RecipeIngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.RecipeMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UnitMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeIngredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeSuggestion;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.DigitalStorageRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeSuggestionRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.CookingService;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemService;
import at.ac.tuwien.sepr.groupphase.backend.service.RecipeIngredientService;
import at.ac.tuwien.sepr.groupphase.backend.service.UnitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CookingServiceImpl implements CookingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final RestTemplate restTemplate;
    private final DigitalStorageServiceImpl digitalStorageService;
    private final String apiKey = "3b683601a4f44cd38d367ab0a1db032d";
    private final RecipeSuggestionRepository repository;
    private final RecipeIngredientService ingredientService;
    private final RecipeIngredientMapper ingredientMapper;
    private final DigitalStorageRepository storageRepository;
    private final RecipeMapper recipeMapper;
    private final RecipeIngredientMapper recipeIngredientMapper;
    private final UnitService unitService;
    private final UnitMapper unitMapper;
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final String apiUrl = "https://api.spoonacular.com/recipes/findByIngredients";

    public CookingServiceImpl(RestTemplate restTemplate,
                              RecipeSuggestionRepository repository,
                              DigitalStorageServiceImpl digitalStorageService,
                              RecipeIngredientService ingredientService,
                              RecipeIngredientMapper ingredientMapper,
                              DigitalStorageRepository storageRepository,
                              RecipeMapper recipeMapper,
                              RecipeIngredientMapper recipeIngredientMapper,
                              UnitService unitService,
                              UnitMapper unitMapper,
                              ItemService itemService,
                              ItemMapper itemMapper) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.digitalStorageService = digitalStorageService;
        this.ingredientService = ingredientService;
        this.ingredientMapper = ingredientMapper;
        this.storageRepository = storageRepository;
        this.recipeMapper = recipeMapper;
        this.recipeIngredientMapper = recipeIngredientMapper;
        this.unitService = unitService;
        this.unitMapper = unitMapper;
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }

    @Override
    public List<RecipeSuggestionDto> getRecipeSuggestion(Long storId, String type) throws ValidationException {

        List<ItemListDto> alwaysInStockItems = digitalStorageService.searchItems(storId, new ItemSearchDto(null, true, null, null, null));
        List<ItemListDto> notAlwaysInStockItems = digitalStorageService.searchItems(storId, new ItemSearchDto(null, false, null, null, null));

        List<ItemListDto> items = new LinkedList<>();
        items.addAll(alwaysInStockItems);
        items.addAll(notAlwaysInStockItems);

        String requestString = getRequestStringForRecipeSearch(items);
        ResponseEntity<List<RecipeDto>> exchange = restTemplate.exchange(requestString, HttpMethod.GET, null, new ParameterizedTypeReference<List<RecipeDto>>() {
        });

        String newReqString = "https://api.spoonacular.com/recipes/informationBulk?apiKey=" + apiKey + "&ids=";
        List<RecipeSuggestionDto> recipeSuggestions = new LinkedList<>();
        if (exchange.getBody() != null) {
            for (RecipeDto recipeDto : exchange.getBody()) {
                String recipeId = String.valueOf(recipeDto.id());
                newReqString += "," + recipeId;
            }
        }
        newReqString += "&includeNutrition=false";
        ResponseEntity<List<RecipeSuggestionDto>> response = restTemplate.exchange(newReqString, HttpMethod.GET, null, new ParameterizedTypeReference<List<RecipeSuggestionDto>>() {
        });

        List<RecipeSuggestionDto> toReturn = getRecipeSuggestionDtos(response, exchange);

        if (type != null) {
            toReturn = filterSuggestions(toReturn, type);
        }

        toReturn = saveUnitsAlsUnits(toReturn);

        return toReturn;
    }

    private List<RecipeSuggestionDto> filterSuggestions(List<RecipeSuggestionDto> recipeSuggestions, String type) {
        List<String> filterTypes = new ArrayList<>();
        if (type.equals("breakfast")) {
            filterTypes.add("breakfast");
            filterTypes.add("snack");
            filterTypes.add("dessert");
            filterTypes.add("brunch");
            filterTypes.add("morning meal");
        }
        if (type.equals("main dish")) {
            filterTypes.add("main course");
            filterTypes.add("lunch");
            filterTypes.add("dinner");
            filterTypes.add("soup");

        }
        if (type.equals("side dish")) {
            filterTypes.add("salad");
            filterTypes.add("side dish");
            filterTypes.add("appetizer");
            filterTypes.add("snack");
            filterTypes.add("fingerfood");
            filterTypes.add("marinade");
            filterTypes.add("starter");
            filterTypes.add("antipasti");
        }

        Map<String, RecipeSuggestionDto> results = new HashMap<>();


        for (String filter : filterTypes) {
            loop2:
            for (RecipeSuggestionDto suggestionDto : recipeSuggestions) {
                for (String typeOfDish : suggestionDto.dishTypes()) {
                    if (filter.equals(typeOfDish)) {
                        results.put(suggestionDto.title(), suggestionDto);
                        break;
                    }
                }
            }
        }
        List<RecipeSuggestionDto> recipesToRet = new ArrayList<>();
        for (Map.Entry<String, RecipeSuggestionDto> recipe : results.entrySet()) {
            recipesToRet.add(recipe.getValue());
        }

        return recipesToRet;
    }

    private static List<RecipeSuggestionDto> getRecipeSuggestionDtos(ResponseEntity<List<RecipeSuggestionDto>> response, ResponseEntity<List<RecipeDto>> exchange) {
        List<RecipeSuggestionDto> toReturn = new LinkedList<>();
        if (response.getBody() != null) {
            for (int i = 0; i < response.getBody().size(); i++) {
                RecipeDto hereWeHaveMissIng = exchange.getBody().get(i);
                RecipeSuggestionDto details = response.getBody().get(i);
                RecipeSuggestionDto toAdd = new RecipeSuggestionDto(
                    details.id(),
                    details.title(),
                    details.servings(),
                    details.readyInMinutes(),
                    details.extendedIngredients(),
                    details.summary(),
                    hereWeHaveMissIng.missedIngredients(),
                    details.dishTypes()
                );
                toReturn.add(toAdd);
            }
        }
        return toReturn;
    }

    private List<RecipeSuggestionDto> saveUnitsAlsUnits(List<RecipeSuggestionDto> recipes) {

        List<RecipeSuggestionDto> updatedRecipeList = new LinkedList<>();
        for (RecipeSuggestionDto recipeSuggestion : recipes) {
            List<RecipeIngredientDto> updatedIngredients = new LinkedList<>();
            List<RecipeIngredientDto> updatedMissedIngredients = new LinkedList<>();
            for (RecipeIngredientDto recipeIngredient : recipeSuggestion.extendedIngredients()) {
                UnitDto unitDto;
                if (recipeIngredient.unit().isEmpty()) {
                    unitDto = unitMapper.entityToUnitDto(unitService.findByName("pcs"));
                } else {
                    unitDto = unitMapper.entityToUnitDto(unitService.findByName(recipeIngredient.unit()));
                }
                updatedIngredients.add(new RecipeIngredientDto(recipeIngredient.id(),
                    recipeIngredient.name(),
                    recipeIngredient.unit(),
                    unitDto,
                    recipeIngredient.amount()));
            }
            for (RecipeIngredientDto recipeIngredient : recipeSuggestion.missedIngredients()) {
                UnitDto unitDto;
                if (recipeIngredient.unit().isEmpty()) {
                    unitDto = unitMapper.entityToUnitDto(unitService.findByName("pcs"));
                } else {
                    unitDto = unitMapper.entityToUnitDto(unitService.findByName(recipeIngredient.unit()));
                }
                updatedMissedIngredients.add(new RecipeIngredientDto(recipeIngredient.id(),
                    recipeIngredient.name(),
                    recipeIngredient.unit(),
                    unitDto,
                    recipeIngredient.amount()));
            }
            updatedRecipeList.add(new RecipeSuggestionDto(recipeSuggestion.id(),
                recipeSuggestion.title(),
                recipeSuggestion.servings(),
                recipeSuggestion.readyInMinutes(),
                updatedIngredients,
                recipeSuggestion.summary(),
                updatedMissedIngredients,
                recipeSuggestion.dishTypes()));
        }
        return updatedRecipeList;
    }

    @Override
    @Cacheable("addresses")
    public RecipeDetailDto getRecipeDetails(Long recipeId) {
        String reqString = "https://api.spoonacular.com/recipes/" + recipeId + "/information" + "?apiKey=" + apiKey + "&includeNutrition=false";
        ResponseEntity<RecipeDetailDto> response = restTemplate.exchange(reqString, HttpMethod.GET, null, new ParameterizedTypeReference<RecipeDetailDto>() {
        });

        String stepsReqString = "https://api.spoonacular.com/recipes/" + recipeId + "/analyzedInstructions" + "?apiKey=" + apiKey + "&stepBreakdown=true";
        ResponseEntity<List<CookingSteps>> responseSteps = restTemplate.exchange(stepsReqString, HttpMethod.GET, null, new ParameterizedTypeReference<List<CookingSteps>>() {
        });

        RecipeDetailDto recipeDetailDto = response.getBody();
        CookingSteps steps = null;
        if (responseSteps.getBody() != null) {
            steps = responseSteps.getBody().get(0);
        }
        if (recipeDetailDto != null) {
            return new RecipeDetailDto(
                recipeId,
                recipeDetailDto.title(),
                recipeDetailDto.servings(),
                recipeDetailDto.readyInMinutes(),
                recipeDetailDto.extendedIngredients(),
                recipeDetailDto.summary(),
                steps
            );
        }
        return null;
    }

    @Override
    public List<RecipeSuggestionDto> getCookbook() throws ValidationException {
        List<RecipeSuggestionDto> recipesDto = new LinkedList<>();
        List<RecipeSuggestion> recipes = repository.findAll();
        for (RecipeSuggestion recipe : recipes) {
            recipesDto.add(recipeMapper.entityToRecipeSuggestionDto(recipe));
        }
        return recipesDto;
    }

    @Override
    public RecipeSuggestion createCookbookRecipe(RecipeSuggestionDto recipe) throws ConflictException {
        List<RecipeIngredient> ingredientList = ingredientService.createAll(recipe.extendedIngredients());
        RecipeSuggestion recipeEntity = recipeMapper.dtoToEntity(recipe, ingredientList);
        RecipeSuggestion createdRecipe = repository.save(recipeEntity);
        createdRecipe.setExtendedIngredients(ingredientList);
        return createdRecipe;
    }

    @Override
    public Optional<RecipeSuggestion> getCookbookRecipe(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<RecipeSuggestion> recipe = repository.findById(id);
        return recipe;
    }

    @Override
    public RecipeSuggestion updateCookbookRecipe(RecipeSuggestionDto recipe) throws ConflictException {
        RecipeSuggestion oldRecipe = this.getCookbookRecipe(recipe.id())
            .orElseThrow(() -> new NotFoundException("Given Id does not exist in the Database!"));

        oldRecipe.setTitle(recipe.title());
        oldRecipe.setSummary(recipe.summary());
        oldRecipe.setReadyInMinutes(recipe.readyInMinutes());
        oldRecipe.setServings(recipe.servings());

        List<RecipeIngredient> ingredientList = ingredientService.createAll(recipe.extendedIngredients());
        oldRecipe.getExtendedIngredients().clear();
        oldRecipe.getExtendedIngredients().addAll(ingredientList);


        RecipeSuggestion updatedRecipe = repository.save(oldRecipe);
        updatedRecipe.setExtendedIngredients(ingredientList);
        return updatedRecipe;
    }

    @Override
    public RecipeSuggestion deleteCookbookRecipe(Long id) {
        RecipeSuggestion deletedRecipe = this.getCookbookRecipe(id).orElseThrow(() -> new NotFoundException("Given Id does not exists in the Database!"));
        repository.delete(deletedRecipe);
        return deletedRecipe;
    }

    @Override
    public RecipeSuggestionDto getMissingIngredients(Long id) {
        Optional<RecipeSuggestion> recipeEntity = repository.findById(id);
        Optional<RecipeSuggestionDto> recipeDto = recipeEntity.map(currentRecipe -> {
            RecipeSuggestionDto recipeSuggestionDto = recipeMapper.entityToRecipeSuggestionDto(currentRecipe);
            if (recipeSuggestionDto != null) {
                List<RecipeIngredientDto> missingIngredients = new LinkedList<>();
                for (RecipeIngredientDto ingredient : recipeSuggestionDto.extendedIngredients()) {
                    List<Item> items = storageRepository.getItemWithGeneralName(1L, ingredient.name());
                    if (items.isEmpty()) {
                        missingIngredients.add(ingredient);
                    }
                    for (Item item : items) {
                        if (!unitMapper.entityToUnitDto(item.getUnit()).equals(ingredient.unitEnum())) {
                            missingIngredients.add(ingredient);
                        } else if (item.getQuantityCurrent() < ingredient.amount()) {
                            RecipeIngredientDto newIngredient = new RecipeIngredientDto(
                                ingredient.id(),
                                ingredient.name(),
                                ingredient.unit(),
                                new UnitDto(" ", 1L, null),
                                ingredient.amount() - item.getQuantityCurrent()
                            );
                            missingIngredients.add(newIngredient);
                        }
                    }
                }
                RecipeSuggestionDto newRecipe = new RecipeSuggestionDto(recipeSuggestionDto.id(), recipeSuggestionDto.title(), recipeSuggestionDto.servings(),
                    recipeSuggestionDto.readyInMinutes(), recipeSuggestionDto.extendedIngredients(), recipeSuggestionDto.summary(),
                    missingIngredients, recipeSuggestionDto.dishTypes());
                recipeSuggestionDto = newRecipe;
            }
            return recipeSuggestionDto;
        });
        return recipeDto.orElse(null);
    }

    @Override
    public RecipeSuggestionDto cookRecipe(RecipeSuggestionDto recipeToCook) throws ValidationException, ConflictException {
        List<RecipeIngredientDto> ingredientToRemoveFromStorage = recipeToCook.extendedIngredients();
        for (RecipeIngredientDto recipeIngredientDto : ingredientToRemoveFromStorage) {
            List<Item> items = storageRepository.getItemWithGeneralName(1L, recipeIngredientDto.name());

            for (Item item : items) {
                if (unitMapper.entityToUnitDto(item.getUnit()).equals(recipeIngredientDto.unitEnum())) {
                    if (item.getQuantityCurrent() < recipeIngredientDto.amount()) {
                        ItemDto updatedItem = itemMapper.entityToDto(item).withUpdatedQuantity((long) (item.getQuantityCurrent() - recipeIngredientDto.amount()));
                        itemService.update(updatedItem);
                    }
                }
            }
        }
        return recipeToCook;
    }

    private String getRequestStringForRecipeSearch(List<ItemListDto> items) {
        List<String> ingredients = new LinkedList<>();
        for (ItemListDto item : items) {
            ingredients.add(item.generalName());
        }

        String requestString = apiUrl;
        requestString += "?apiKey=" + apiKey;
        boolean isFirst = true;
        for (String ingredient : ingredients) {
            if (isFirst) {
                requestString += "&ingredients=" + ingredient;
                isFirst = false;
            } else {
                requestString += ",+" + ingredient;
            }
        }
        requestString += "&number=1";
        return requestString + "&ranking=2";
    }

    private String getRequestStringForDetails(String recipeId) {
        return "https://api.spoonacular.com/recipes/" + recipeId + "/analyzedInstructions" + "?apiKey=" + apiKey;
    }
}
