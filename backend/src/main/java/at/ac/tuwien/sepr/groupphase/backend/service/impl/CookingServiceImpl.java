package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.CookbookDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.CookingSteps;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeIngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.CookbookMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.RecipeIngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.RecipeMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UnitMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cookbook;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeIngredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeSuggestion;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.CookbookRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.DigitalStorageRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeSuggestionRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.CookingService;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemService;
import at.ac.tuwien.sepr.groupphase.backend.service.RecipeIngredientService;
import at.ac.tuwien.sepr.groupphase.backend.service.UnitService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.authenticator.Authorization;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.CookbookValidator;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.RecipeValidator;
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
    private final RecipeValidator recipeValidator;
    private final CookbookValidator cookbookValidator;
    private final SharedFlatService sharedFlatService;
    private final Authorization authorization;
    private final CookbookMapper cookbookMapper;
    private final CookbookRepository cookbookRepository;
    private final UserService userService;
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
                              ItemMapper itemMapper, RecipeValidator recipeValidator,
                              CookbookValidator cookbookValidator, SharedFlatService sharedFlatService,
                              Authorization authorization, CookbookMapper cookbookMapper,
                              CookbookRepository cookbookRepository, UserService userService) {
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
        this.recipeValidator = recipeValidator;
        this.cookbookValidator = cookbookValidator;
        this.sharedFlatService = sharedFlatService;
        this.authorization = authorization;
        this.cookbookMapper = cookbookMapper;
        this.cookbookRepository = cookbookRepository;
        this.userService = userService;
    }

    @Override
    public List<RecipeSuggestionDto> getRecipeSuggestion(String type, String jwt) throws ValidationException, ConflictException, AuthenticationException {


        List<ItemListDto> alwaysInStockItems = digitalStorageService.searchItems(new ItemSearchDto(null, true, null, null, null), jwt);
        List<ItemListDto> notAlwaysInStockItems = digitalStorageService.searchItems(new ItemSearchDto(null, false, null, null, null), jwt);

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
    public Cookbook createCookbook(CookbookDto cookbookDto, String jwt) throws ValidationException, ConflictException, AuthenticationException {

        cookbookValidator.validateForCreate(cookbookDto);


        List<Long> allowedUser = sharedFlatService.findById(
                cookbookDto.sharedFlat().getId(),
                jwt
            ).getUsers().stream()
            .map(ApplicationUser::getId)
            .toList();
        authorization.authenticateUser(
            jwt,
            allowedUser,
            "The given cookbook does not belong to the user's shared flat!"
        );

        Cookbook cookbook = cookbookMapper.dtoToEntity(cookbookDto);

        return cookbookRepository.save(cookbook);
    }

    @Override
    public List<Cookbook> findAllCookbooks(String jwt) throws AuthenticationException {
        ApplicationUser applicationUser = userService.getUser(jwt);
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exists"));
        }
        return cookbookRepository.findBySharedFlatIs(applicationUser.getSharedFlat());
    }

    @Override
    public List<RecipeSuggestionDto> getCookbook(String jwt) throws ValidationException, AuthenticationException {
        List<RecipeSuggestionDto> recipesDto = new LinkedList<>();
        Long cookbookId = this.getCookbookIdForUser(jwt);
        Cookbook cookbook = cookbookRepository.findById(cookbookId).orElseThrow(() -> new NotFoundException("Given Id does not exist in the Database!"));
        List<RecipeSuggestion> recipes = cookbook.getRecipes();
        for (RecipeSuggestion recipe : recipes) {
            recipesDto.add(recipeMapper.entityToRecipeSuggestionDto(recipe));
        }
        return recipesDto;
    }

    @Override
    public RecipeSuggestion createCookbookRecipe(RecipeSuggestionDto recipe, String jwt) throws ConflictException, ValidationException,
        AuthenticationException {
        recipeValidator.validateForCreate(recipe);
        List<RecipeIngredient> ingredientList = ingredientService.createAll(recipe.extendedIngredients());
        RecipeSuggestion recipeEntity = recipeMapper.dtoToEntity(recipe, ingredientList);
        Long cookbookId = this.getCookbookIdForUser(jwt);
        Cookbook cookbook = cookbookRepository.findById(cookbookId).orElseThrow(() -> new NotFoundException("Given Id does not exist in the Database!"));
        recipeEntity.setCookbook(cookbook);
        RecipeSuggestion createdRecipe = repository.save(recipeEntity);
        createdRecipe.setExtendedIngredients(ingredientList);
        return createdRecipe;
    }

    @Override
    public Optional<RecipeSuggestion> getCookbookRecipe(Long id, String jwt) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<RecipeSuggestion> recipe = repository.findById(id);
        return recipe;
    }

    @Override
    public RecipeSuggestion updateCookbookRecipe(RecipeSuggestionDto recipe, String jwt) throws ValidationException, AuthenticationException {
        recipeValidator.validateForUpdate(recipe);
        RecipeSuggestion oldRecipe = this.getCookbookRecipe(recipe.id(), jwt)
            .orElseThrow(() -> new NotFoundException("Given Id does not exist in the Database!"));
        Long cookbookId = this.getCookbookIdForUser(jwt);
        Cookbook cookbook = cookbookRepository.findById(cookbookId).orElseThrow(() -> new NotFoundException("Given Id does not exist in the Database!"));

        oldRecipe.setTitle(recipe.title());
        oldRecipe.setSummary(recipe.summary());
        oldRecipe.setReadyInMinutes(recipe.readyInMinutes());
        oldRecipe.setServings(recipe.servings());
        oldRecipe.setCookbook(cookbook);

        List<RecipeIngredient> ingredientList = ingredientService.createAll(recipe.extendedIngredients());
        oldRecipe.getExtendedIngredients().clear();
        oldRecipe.getExtendedIngredients().addAll(ingredientList);


        RecipeSuggestion updatedRecipe = repository.save(oldRecipe);
        updatedRecipe.setExtendedIngredients(ingredientList);
        return updatedRecipe;
    }

    @Override
    public RecipeSuggestion deleteCookbookRecipe(Long id, String jwt) throws AuthenticationException {
        RecipeSuggestion deletedRecipe = this.getCookbookRecipe(id, jwt).orElseThrow(() -> new NotFoundException("Given Id does not exists in the Database!"));
        this.getCookbookIdForUser(jwt);
        repository.delete(deletedRecipe);
        return deletedRecipe;
    }

    @Override
    public RecipeSuggestionDto getMissingIngredients(Long id, String jwt) throws AuthenticationException {
        Long storId = this.getStorIdForUser(jwt);
        Optional<RecipeSuggestion> recipeEntity = repository.findById(id);
        Optional<RecipeSuggestionDto> recipeDto = recipeEntity.map(currentRecipe -> {
            RecipeSuggestionDto recipeSuggestionDto = recipeMapper.entityToRecipeSuggestionDto(currentRecipe);
            if (recipeSuggestionDto != null) {
                List<RecipeIngredientDto> missingIngredients = new LinkedList<>();
                for (RecipeIngredientDto ingredient : recipeSuggestionDto.extendedIngredients()) {
                    List<Item> items = storageRepository.getItemWithGeneralName(storId, ingredient.name());
                    if (items.isEmpty()) {
                        missingIngredients.add(ingredient);
                        continue;
                    }

                    Unit ingredientUnit = unitMapper.unitDtoToEntity(ingredient.unitEnum());
                    if (!getMinUnit(ingredientUnit).equals(getMinUnit(items.get(0).getUnit()))) {
                        missingIngredients.add(ingredient);
                        continue;
                    }
                    try {
                        Double ingAmountMin = unitService.convertUnits(ingredientUnit, getMinUnit(ingredientUnit), ingredient.amount());
                        Double itemQuantityTotal = getItemQuantityTotalInMinQuantity(items);

                        if (ingAmountMin > itemQuantityTotal) {
                            if (ingredientUnit.getConvertFactor() == null) {
                                ingredientUnit.setConvertFactor(1L);
                            }
                            if (ingAmountMin < ingredientUnit.getConvertFactor()) {

                                RecipeIngredientDto newIngredient = new RecipeIngredientDto(
                                    ingredient.id(),
                                    ingredient.name(),
                                    getMinUnit(ingredientUnit).getName(),
                                    unitMapper.entityToUnitDto(getMinUnit(ingredientUnit)),
                                    ingAmountMin - itemQuantityTotal
                                );
                                missingIngredients.add(newIngredient);
                            } else {

                                Double updatedQuantity = unitService.convertUnits(getMinUnit(ingredientUnit), items.get(0).getUnit(), ingAmountMin - itemQuantityTotal);
                                RecipeIngredientDto newIngredient = new RecipeIngredientDto(
                                    ingredient.id(),
                                    ingredient.name(),
                                    items.get(0).getUnit().getName(),
                                    unitMapper.entityToUnitDto(items.get(0).getUnit()),
                                    updatedQuantity
                                );
                                missingIngredients.add(newIngredient);
                            }
                        }


                    } catch (ValidationException | ConflictException e) {
                        throw new RuntimeException(e);
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
    public RecipeSuggestionDto cookRecipe(RecipeSuggestionDto recipeToCook, String jwt) throws ValidationException, ConflictException, AuthenticationException {
        recipeValidator.validateForCook(recipeToCook);
        Long storId = this.getStorIdForUser(jwt);
        List<RecipeIngredientDto> ingredientToRemoveFromStorage = recipeToCook.extendedIngredients();
        for (RecipeIngredientDto recipeIngredientDto : ingredientToRemoveFromStorage) {
            List<Item> items = storageRepository.getItemWithGeneralName(storId, recipeIngredientDto.name());
            Unit ingredientUnit = unitMapper.unitDtoToEntity(recipeIngredientDto.unitEnum());
            Double ingAmountMin = unitService.convertUnits(ingredientUnit, getMinUnit(ingredientUnit), recipeIngredientDto.amount());
            List<Item> itemsWithMinUnits = minimizeUnits(items);
            for (int i = 0; i < itemsWithMinUnits.size(); i++) {

                Item item = itemsWithMinUnits.get(i);

                if (item.getQuantityCurrent() >= ingAmountMin) {
                    if (item.getUnit().equals(items.get(i).getUnit())) {
                        ItemDto updatedItem = itemMapper.entityToDto(item).withUpdatedQuantity((item.getQuantityCurrent() - ingAmountMin));
                        itemService.update(updatedItem, jwt);
                    } else {
                        Double updatedQuantity = unitService.convertUnits(item.getUnit(), items.get(i).getUnit(), item.getQuantityCurrent() - ingAmountMin);
                        item.setUnit(items.get(i).getUnit());
                        ItemDto updatedItem = itemMapper.entityToDto(item).withUpdatedQuantity(updatedQuantity);
                        itemService.update(updatedItem, jwt);
                    }
                    break;
                } else {
                    ingAmountMin -= item.getQuantityCurrent();
                    item.setUnit(items.get(i).getUnit());
                    ItemDto updatedItem = itemMapper.entityToDto(item).withUpdatedQuantity(0.0);
                    itemService.update(updatedItem, jwt);

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
        requestString += "&number=2";
        return requestString + "&ranking=2";
    }

    private String getRequestStringForDetails(String recipeId) {
        return "https://api.spoonacular.com/recipes/" + recipeId + "/analyzedInstructions" + "?apiKey=" + apiKey;
    }

    private Unit getMinUnit(Unit unit) {
        if (unit.getSubUnit().isEmpty()) {
            return unit;
        }
        for (Unit subUnit : unit.getSubUnit()) {
            return subUnit;
        }

        return null;
    }

    private Double getItemQuantityTotalInMinQuantity(List<Item> items) throws ValidationException, ConflictException {

        Double toRet = 0.0;

        Unit test = getMinUnit(items.get(0).getUnit());
        for (Item item : items) {
            if (item.getUnit().equals(test)) {
                toRet += item.getQuantityCurrent();
            } else {
                Double convert = unitService.convertUnits(item.getUnit(), test, item.getQuantityCurrent());
                toRet += convert;
            }
        }
        return toRet;
    }

    private List<Item> minimizeUnits(List<Item> items) {

        List<Item> minimizedItems = new LinkedList<>();


        for (Item item : items) {
            Unit minUnit = getMinUnit(item.getUnit());
            double convertedQuantity = unitService.convertUnits(item.getUnit(), minUnit, item.getQuantityCurrent());

            Item minimizedItem = new Item();

            minimizedItem.setItemId(item.getItemId());
            minimizedItem.setUnit(minUnit);
            minimizedItem.setQuantityCurrent(convertedQuantity);
            minimizedItem.setEan(item.getEan());
            minimizedItem.setGeneralName(item.getGeneralName());
            minimizedItem.setProductName(item.getProductName());
            minimizedItem.setBrand(item.getBrand());
            minimizedItem.setQuantityTotal(item.getQuantityTotal());
            minimizedItem.setExpireDate(item.getExpireDate());
            minimizedItem.setDescription(item.getDescription());
            minimizedItem.setPriceInCent(item.getPriceInCent());
            minimizedItem.setBoughtAt(item.getBoughtAt());
            minimizedItem.setStorage(item.getStorage());
            minimizedItem.setIngredientList(item.getIngredientList());

            minimizedItems.add(minimizedItem);
        }

        return minimizedItems;

    }

    private Long getCookbookIdForUser(String jwt) throws AuthenticationException {
        List<Cookbook> cookbookList = findAllCookbooks(jwt);
        Cookbook matchingCookbook = null;
        if (!cookbookList.isEmpty()) {
            matchingCookbook = cookbookList.stream().toList().get(0);
        }
        if (matchingCookbook != null) {
            List<Long> allowedUser = sharedFlatService.findById(
                    matchingCookbook.getSharedFlat().getId(),
                    jwt
                ).getUsers().stream()
                .map(ApplicationUser::getId)
                .toList();


            authorization.authenticateUser(
                jwt,
                allowedUser,
                "The given cookbook does not belong to the user's shared flat!"
            );


            return matchingCookbook.getId();
        } else {
            return null;
        }
    }

    private Long getStorIdForUser(String jwt) throws AuthenticationException {
        List<DigitalStorage> digitalStorageList = digitalStorageService.findAll(null, jwt);
        DigitalStorage matchingDigitalStorage = null;
        if (!digitalStorageList.isEmpty()) {
            matchingDigitalStorage = digitalStorageList.stream().toList().get(0);
        }
        if (matchingDigitalStorage != null) {
            List<Long> allowedUser = sharedFlatService.findById(
                    matchingDigitalStorage.getSharedFlat().getId(),
                    jwt
                ).getUsers().stream()
                .map(ApplicationUser::getId)
                .toList();


            authorization.authenticateUser(
                jwt,
                allowedUser,
                "The given digital storage does not belong to the user's shared flat!"
            );


            return matchingDigitalStorage.getStorId();
        } else {
            return null;
        }
    }

}
