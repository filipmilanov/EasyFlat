package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.CookingSteps;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.CookingStepsBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeDetailDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeIngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeIngredientDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.StepBuilder;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlternativeName;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemCache;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeSuggestion;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import com.deepl.api.DeepLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class CookingServiceTest {


    @Autowired
    private DigitalStorageService digitalStorageService;

    @Autowired
    private CookingService cookingService;

    @Autowired
    private TestDataGenerator testDataGenerator;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    private ApplicationUser applicationUser;


    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();
        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);

    }

    @Test
    void testGetRecipeSuggestionFromAPI() throws ValidationException, ConflictException, AuthenticationException, AuthorizationException, DeepLException, InterruptedException {
        when(itemRepository.findAllByDigitalStorage_StorageId(any())).thenReturn(getMockedItemsWithoutMatching());
        mockAPIResponse();

        // when
        List<RecipeSuggestionDto> result = cookingService.getRecipeSuggestion(null);


        // then

        RecipeSuggestionDto actualRecipeSuggestionDto = result.get(0); // Assuming we are expecting a single result
        RecipeSuggestionDto expectedRecipeDto = getExpectedRecipeSuggestionDtoWithUnits();

        assertAll(
            () -> assertThat(actualRecipeSuggestionDto.id()).isEqualTo(expectedRecipeDto.id()),
            () -> assertThat(actualRecipeSuggestionDto.title()).isEqualTo(expectedRecipeDto.title()),
            () -> assertThat(actualRecipeSuggestionDto.servings()).isEqualTo(expectedRecipeDto.servings()),
            () -> assertThat(actualRecipeSuggestionDto.readyInMinutes()).isEqualTo(expectedRecipeDto.readyInMinutes()),
            () -> assertThat(actualRecipeSuggestionDto.summary()).isEqualTo(expectedRecipeDto.summary()),
            () -> assertThat(actualRecipeSuggestionDto.extendedIngredients())
                .usingRecursiveComparison()
                .ignoringFields("matchedItem")
                .isEqualTo(expectedRecipeDto.extendedIngredients())
        );


    }

    @Test
    void testGetRecipeDetailsFromAPI() {
        mockAPIResponseForDetails();

        // when
        RecipeDetailDto actualRecipeDetailDto = cookingService.getRecipeDetails(1L);
        RecipeDetailDto expectedRecipeDetailDto = getExpectedRecipeDetailDtoWithUnitsAndSteps();

        // then
        assertAll(
            () -> assertThat(actualRecipeDetailDto.id()).isEqualTo(expectedRecipeDetailDto.id()),
            () -> assertThat(actualRecipeDetailDto.title()).isEqualTo(expectedRecipeDetailDto.title()),
            () -> assertThat(actualRecipeDetailDto.servings()).isEqualTo(expectedRecipeDetailDto.servings()),
            () -> assertThat(actualRecipeDetailDto.readyInMinutes()).isEqualTo(expectedRecipeDetailDto.readyInMinutes()),
            () -> assertThat(actualRecipeDetailDto.summary()).isEqualTo(expectedRecipeDetailDto.summary()),
            () -> assertThat(actualRecipeDetailDto.extendedIngredients()).isEqualTo(expectedRecipeDetailDto.extendedIngredients()),
            () -> assertThat(actualRecipeDetailDto.steps()).isEqualTo(expectedRecipeDetailDto.steps())
        );
    }

    @Test
    void testCookRecipeReturnTheCookedRecipe() throws ValidationException, ConflictException, AuthenticationException, AuthorizationException {
        // given
        Set<UnitDto> subUnit = new HashSet<>();
        subUnit.add(new UnitDto("g", null, null));
        RecipeSuggestionDto testRecipe = RecipeSuggestionDtoBuilder.builder()
            .id(1L)
            .title("Test recipe")
            .servings(5)
            .readyInMinutes(10)
            .extendedIngredients(Arrays.asList(
                RecipeIngredientDtoBuilder.builder()
                    .id(1L)
                    .name("apples")
                    .unit("kg")
                    .unitEnum(UnitDtoBuilder.builder()
                        .name("kg")
                        .convertFactor(1000L)
                        .subUnit(subUnit)
                        .build())
                    .amount(1.0)
                    .build(),
                RecipeIngredientDtoBuilder.builder()
                    .id(2L)
                    .name("flour")
                    .unit("kg")
                    .unitEnum(UnitDtoBuilder.builder()
                        .name("kg")
                        .convertFactor(1000L)
                        .subUnit(subUnit)
                        .build())
                    .amount(0.5)
                    .build(),
                RecipeIngredientDtoBuilder.builder()
                    .id(3L)
                    .name("sugar")
                    .unit("kg")
                    .unitEnum(UnitDtoBuilder.builder()
                        .name("kg")
                        .convertFactor(1000L)
                        .subUnit(subUnit)
                        .build())
                    .amount(0.2)
                    .build()))
            .summary("How to cook")
            .build();

        // when
        RecipeSuggestionDto result = cookingService.cookRecipe(testRecipe);

        // then
        assertAll(
            () -> assertThat(result).isNotNull(),
            () -> assertThat(result.id()).isEqualTo(testRecipe.id()),
            () -> assertThat(result.title()).isEqualTo(testRecipe.title()),
            () -> assertThat(result.servings()).isEqualTo(testRecipe.servings()),
            () -> assertThat(result.readyInMinutes()).isEqualTo(testRecipe.readyInMinutes()),
            () -> assertThat(result.extendedIngredients()).usingElementComparatorIgnoringFields("id").isEqualTo(testRecipe.extendedIngredients()),
            () -> assertThat(result.summary()).isEqualTo(testRecipe.summary())
        );

    }

    @Test
    void testCookRecipeRemoveItemsQuantityFromStorage() throws ValidationException, ConflictException, AuthenticationException, AuthorizationException {
        // given
        Set<UnitDto> subUnit = new HashSet<>();
        subUnit.add(new UnitDto("g", null, null));
        RecipeSuggestionDto testRecipe = RecipeSuggestionDtoBuilder.builder()
            .id(1L)
            .title("Test recipe")
            .servings(5)
            .readyInMinutes(10)
            .extendedIngredients(Arrays.asList(
                RecipeIngredientDtoBuilder.builder()
                    .id(1L)
                    .name("apples")
                    .unit("kg")
                    .unitEnum(UnitDtoBuilder.builder()
                        .name("kg")
                        .convertFactor(1000L)
                        .subUnit(subUnit)
                        .build())
                    .amount(1.0)
                    .build(),
                RecipeIngredientDtoBuilder.builder()
                    .id(2L)
                    .name("flour")
                    .unit("kg")
                    .unitEnum(UnitDtoBuilder.builder()
                        .name("kg")
                        .convertFactor(1000L)
                        .subUnit(subUnit)
                        .build())
                    .amount(0.5)
                    .build(),
                RecipeIngredientDtoBuilder.builder()
                    .id(3L)
                    .name("sugar")
                    .unit("kg")
                    .unitEnum(UnitDtoBuilder.builder()
                        .name("kg")
                        .convertFactor(1000L)
                        .subUnit(subUnit)
                        .build())
                    .amount(0.2)
                    .build()))
            .summary("How to cook")
            .build();

        ItemSearchDto searchParamsIS = new ItemSearchDto(false, null, null, null, null);
        ItemSearchDto searchParamsAIS = new ItemSearchDto(true, null, null, null, null);
        List<ItemListDto> itemsFromDigitalStorageIS = digitalStorageService.searchItems(searchParamsIS);
        List<ItemListDto> itemsFromDigitalStorageAIS = digitalStorageService.searchItems(searchParamsAIS);
        List<ItemListDto> items = new LinkedList<>();
        items.addAll(itemsFromDigitalStorageIS);
        items.addAll(itemsFromDigitalStorageAIS);
        // when
        RecipeSuggestionDto result = cookingService.cookRecipe(testRecipe);

        List<ItemListDto> itemsFromDigitalStorageIST = digitalStorageService.searchItems(searchParamsIS);
        List<ItemListDto> itemsFromDigitalStorageAIST = digitalStorageService.searchItems(searchParamsAIS);
        List<ItemListDto> itemsT = new LinkedList<>();
        items.addAll(itemsFromDigitalStorageIST);
        items.addAll(itemsFromDigitalStorageAIST);


        for (ItemListDto item : itemsT) {
            for (ItemListDto initialItem : items) {
                if (item.generalName().equals(initialItem.generalName())) {
                    for (RecipeIngredientDto ingredientDto : testRecipe.extendedIngredients()) {
                        if (item.generalName().equals(ingredientDto.name())) {
                            assertThat(item.quantityCurrent()).isEqualTo(initialItem.quantityCurrent() - ingredientDto.amount());
                        }
                    }
                }
            }
        }

    }

    @Test
    @DisplayName("Positive test for creating a valid cookbook recipe")
    void createValidCookbookRecipeShouldSucceed() throws ValidationException, ConflictException, AuthenticationException, AuthorizationException {
        RecipeSuggestionDto recipe = RecipeSuggestionDtoBuilder.builder()
            .title("Test recipe")
            .servings(2)
            .readyInMinutes(20)
            .summary("This is only a test recipe")
            .extendedIngredients(new ArrayList<>())
            .build();

        RecipeSuggestion createdRecipe = cookingService.createCookbookRecipe(recipe);

        assertNotNull(createdRecipe);
        assertEquals(recipe.title(), createdRecipe.getTitle());
        assertEquals(recipe.servings(), createdRecipe.getServings());
        assertEquals(recipe.readyInMinutes(), createdRecipe.getReadyInMinutes());
        assertEquals(recipe.summary(), createdRecipe.getSummary());
        assertNotNull(createdRecipe.getId());
    }

    @Test
    @DisplayName("Negative test for creating a non-valid cookbook recipe")
    void createNonValidCookbookRecipeShouldThrowValidationException() throws ValidationException, ConflictException, AuthenticationException, AuthorizationException {
        RecipeSuggestionDto recipe = RecipeSuggestionDtoBuilder.builder()
            .title("")
            .summary("This is a non-valid test recipe")
            .extendedIngredients(new ArrayList<>())
            .build();

        assertThrows(ValidationException.class, () -> cookingService.createCookbookRecipe(recipe));
    }

    @Test
    @DisplayName("Positive test for updating a cookbook recipe")
    void updateCookbookRecipeShouldSucceed() throws ValidationException, AuthenticationException, NotFoundException, ConflictException, AuthorizationException {
        // Mock data
        RecipeSuggestionDto updatedRecipeDto = RecipeSuggestionDtoBuilder.builder()
            .id(1L)
            .title("Updated Test Recipe")
            .servings(4)
            .readyInMinutes(30)
            .summary("This is an updated test recipe")
            .extendedIngredients(new ArrayList<>())
            .build();


        RecipeSuggestion updatedRecipe = cookingService.updateCookbookRecipe(updatedRecipeDto);

        assertNotNull(updatedRecipe);
        assertEquals(updatedRecipeDto.id(), updatedRecipe.getId());
        assertEquals(updatedRecipeDto.title(), updatedRecipe.getTitle());
        assertEquals(updatedRecipeDto.servings(), updatedRecipe.getServings());
        assertEquals(updatedRecipeDto.readyInMinutes(), updatedRecipe.getReadyInMinutes());
        assertEquals(updatedRecipeDto.summary(), updatedRecipe.getSummary());
    }

    @Test
    @DisplayName("Negative test for updating a non-existing cookbook recipe")
    void updateNonExistingCookbookRecipeShouldThrowNotFoundException() {
        // Mock data for non-existing recipe
        RecipeSuggestionDto updatedRecipeDto = RecipeSuggestionDtoBuilder.builder()
            .id(1000L)
            .title("Updated Test Recipe")
            .servings(2)
            .readyInMinutes(20)
            .summary("This is an updated test recipe")
            .extendedIngredients(new ArrayList<>())
            .build();


        assertThrows(NotFoundException.class, () -> cookingService.updateCookbookRecipe(updatedRecipeDto));
    }

    @Test
    @DisplayName("Test for deleting a cookbook recipe")
    void deleteCookbookRecipeShouldSucceed() throws AuthenticationException, NotFoundException, AuthorizationException {

        RecipeSuggestionDto existing = cookingService.getCookbookRecipe(1L);

        RecipeSuggestion deleted = cookingService.deleteCookbookRecipe(1L);

        assertNotNull(deleted);
        assertEquals(existing.id(), deleted.getId());
        assertEquals(existing.title(), deleted.getTitle());
        assertEquals(existing.summary(), deleted.getSummary());
    }

    @Test
    @DisplayName("Negative test for deleting a non-existing cookbook recipe")
    void deleteNonExistingCookbookRecipeShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> cookingService.deleteCookbookRecipe(1000L));
    }

    @Test
    void matchIngredientThanTheIngredientShouldBeMatchedInGetRecipes() throws AuthorizationException, DeepLException, ValidationException, ConflictException, AuthenticationException, InterruptedException {
        when(itemRepository.findAllByDigitalStorage_StorageId(any())).thenReturn(getMockedItems());
        DigitalStorageItem digitalStorageItem = getMockedItems().get(0);
        mockAPIResponse();

        // when
        List<RecipeSuggestionDto> result = cookingService.getRecipeSuggestion(null);

        RecipeSuggestionDto actualRecipeSuggestionDto = result.get(0);
        //The Parmesan cheese should be matched
        RecipeIngredientDto matchedIngredient = actualRecipeSuggestionDto.extendedIngredients().get(3);
        assertAll(
            () -> assertThat(matchedIngredient.name()).isEqualTo("digitalStorageItem"),
            () -> assertThat(matchedIngredient.matched()).isEqualTo(true),
            () -> assertThat(matchedIngredient.matchedItem().productName()).isEqualTo(digitalStorageItem.getItemCache().getProductName()),
            () -> assertThat(matchedIngredient.amount()).isEqualTo(100),
            () -> assertThat(matchedIngredient.realName()).isEqualTo("Parmesan cheese")
        );
    }

    @Test
    void matchIngredientThanTheIngredientShouldBeMatchedInCookRecipe() throws ValidationException, ConflictException, AuthorizationException, AuthenticationException {
        when(itemRepository.findAllByDigitalStorage_StorageId(any())).thenReturn(getMockedItems());
        DigitalStorageItem digitalStorageItem = getMockedItems().get(0);

        // when
        RecipeSuggestionDto actualRecipeSuggestionDto = cookingService.cookRecipe(getExpectedRecipeSuggestionDtoWithUnits());
        RecipeIngredientDto matchedIngredient = actualRecipeSuggestionDto.extendedIngredients().get(3);
        assertAll(
            () -> assertThat(matchedIngredient.name()).isEqualTo("Parmesan cheese"),
            () -> assertThat(matchedIngredient.amount()).isEqualTo(100)
        );

    }

    @Test
    void matchIngredientThanTheIngredientShouldBeMatchedInRecipeDetailDto(){
        when(itemRepository.findAllByDigitalStorage_StorageId(any())).thenReturn(getMockedItems());
        DigitalStorageItem digitalStorageItem = getMockedItems().get(0);

        mockAPIResponseForDetails();
        //when
        RecipeDetailDto actualRecipeDetailDto = cookingService.getRecipeDetails(1L);

        //then
        RecipeIngredientDto matchedIngredient = actualRecipeDetailDto.extendedIngredients().get(3);
        assertAll(
            () -> assertThat(matchedIngredient.name()).isEqualTo("digitalStorageItem"),
            () -> assertThat(matchedIngredient.matched()).isEqualTo(true),
            () -> assertThat(matchedIngredient.matchedItem().productName()).isEqualTo(digitalStorageItem.getItemCache().getProductName()),
            () -> assertThat(matchedIngredient.amount()).isEqualTo(100),
            () -> assertThat(matchedIngredient.realName()).isEqualTo("Parmesan cheese")
        );
    }

    private void mockAPIResponse() {
        List<RecipeDto> mockedRecipesDtos = getRecipeDtos();
        RecipeSuggestionDto mockedRecipeSuggestionDto = getRecipeSuggestionDtoWithoutUnits();


        ParameterizedTypeReference<List<RecipeDto>> ref = new ParameterizedTypeReference<List<RecipeDto>>() {
        };
        ParameterizedTypeReference<RecipeSuggestionDto> ref2 = new ParameterizedTypeReference<RecipeSuggestionDto>() {
        };
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(ref)))
            .thenReturn(ResponseEntity.ok(mockedRecipesDtos));
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(ref2)))
            .thenReturn(ResponseEntity.ok(mockedRecipeSuggestionDto));

    }

    private void mockAPIResponseForDetails() {
        RecipeDetailDto mockedRecipeDetailDto = getRecipeDetailDtoWithoutUnitsAndSteps();
        List<CookingSteps> mockedSteps = getCookingStepsDto();

        ParameterizedTypeReference<RecipeDetailDto> ref = new ParameterizedTypeReference<RecipeDetailDto>() {
        };
        ParameterizedTypeReference<List<CookingSteps>> ref2 = new ParameterizedTypeReference<List<CookingSteps>>() {
        };
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(ref)))
            .thenReturn(ResponseEntity.ok(mockedRecipeDetailDto));
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(ref2)))
            .thenReturn(ResponseEntity.ok(mockedSteps));

    }

    private RecipeSuggestionDto getRecipeSuggestionDtoWithoutUnits() {
        RecipeSuggestionDto recipeDto2 = RecipeSuggestionDtoBuilder.builder()
            .id(1L)
            .title("Pasta Carbonara")
            .servings(4)
            .readyInMinutes(25)
            .summary("Classic Italian pasta dish with eggs, cheese, pancetta, and black pepper.")
            .extendedIngredients(List.of(
                RecipeIngredientDtoBuilder.builder()
                    .id(4L)
                    .name("Spaghetti")
                    .unit("g")
                    .amount(400.0)
                    .matched(true)
                    .autoMatched(false)
                    .realName("Spaghetti")
                    .matchedItem(null)
                    .build(),
                RecipeIngredientDtoBuilder.builder()
                    .id(5L)
                    .name("Pancetta")
                    .unit("g")
                    .amount(150.0)
                    .matched(true)
                    .autoMatched(false)
                    .realName("Pancetta")
                    .matchedItem(null)
                    .build(),
                RecipeIngredientDtoBuilder.builder()
                    .id(6L)
                    .name("Eggs")
                    .unit("pcs")
                    .amount(3.0)
                    .matched(true)
                    .autoMatched(false)
                    .realName("Eggs")
                    .matchedItem(null)
                    .build(),
                RecipeIngredientDtoBuilder.builder()
                    .id(7L)
                    .name("Parmesan cheese")
                    .unit("g")
                    .amount(100.0)
                    .matched(true)
                    .autoMatched(false)
                    .realName("Parmesan cheese")
                    .matchedItem(null)
                    .build()
            ))
            .build();


        return recipeDto2;
    }

    private RecipeSuggestionDto getExpectedRecipeSuggestionDtoWithUnits() {


        UnitDto gUnit = UnitDtoBuilder.builder()
            .name("g")
            .subUnit(new HashSet<>())
            .build();


        UnitDto pcsUnit = UnitDtoBuilder.builder()
            .name("pcs")
            .subUnit(new HashSet<>())
            .build();


        RecipeSuggestionDto recipeDto2 = RecipeSuggestionDtoBuilder.builder()
            .id(1L)
            .title("Pasta Carbonara")
            .servings(4)
            .readyInMinutes(25)
            .summary("Classic Italian pasta dish with eggs, cheese, pancetta, and black pepper.")
            .extendedIngredients(List.of(
                RecipeIngredientDtoBuilder.builder()
                    .id(4L)
                    .name("Spaghetti")
                    .unit("g")
                    .amount(400.0)
                    .matched(true)
                    .autoMatched(false)
                    .realName("Spaghetti")
                    .matchedItem(null)
                    .unitEnum(gUnit)
                    .build(),
                RecipeIngredientDtoBuilder.builder()
                    .id(5L)
                    .name("Pancetta")
                    .unit("g")
                    .amount(150.0)
                    .matched(true)
                    .autoMatched(false)
                    .realName("Pancetta")
                    .matchedItem(null)
                    .unitEnum(gUnit)
                    .build(),
                RecipeIngredientDtoBuilder.builder()
                    .id(6L)
                    .name("Eggs")
                    .unit("pcs")
                    .amount(3.0)
                    .matched(true)
                    .autoMatched(false)
                    .realName("Eggs")
                    .matchedItem(null)
                    .unitEnum(pcsUnit)
                    .build(),
                RecipeIngredientDtoBuilder.builder()
                    .id(7L)
                    .name("Parmesan cheese")
                    .unit("g")
                    .amount(100.0)
                    .matched(true)
                    .autoMatched(false)
                    .realName("Parmesan cheese")
                    .matchedItem(null)
                    .unitEnum(gUnit)
                    .build()
            ))
            .build();

        return recipeDto2;
    }

    private List<RecipeDto> getRecipeDtos() {
        RecipeDto mockedRecipe1 = RecipeDtoBuilder.builder()
            .id(1L)
            .title("Pasta Carbonara")
            .description("Classic Italian pasta dish with eggs, cheese, pancetta, and black pepper.")
            .image("image1.jpg")
            .missedIngredients(List.of(
                RecipeIngredientDtoBuilder.builder()
                    .id(1L)
                    .name("Ingredient 1")
                    .unit("unit1")
                    .unitEnum(null)
                    .amount(100.0)
                    .matched(true)
                    .autoMatched(false)
                    .realName("Real Ingredient 1")
                    .matchedItem(null)
                    .build()
            ))
            .build();
        List<RecipeDto> toReturn = new LinkedList<>();
        toReturn.add(mockedRecipe1);
        return toReturn;
    }

    private List<DigitalStorageItem> getMockedItems() {

        List<AlternativeName> alternativeNames = new LinkedList<>();
        AlternativeName alternativeName = new AlternativeName();
        alternativeName.setName("Parmesan cheese");
        alternativeNames.add(alternativeName);

        Unit subUnit = new Unit();
        subUnit.setName("g");

        Unit unit = new Unit();
        unit.setName("kg");
        unit.setSubUnit(Set.of(subUnit));
        unit.setConvertFactor(1000L);

        ItemCache itemCache = new ItemCache();
        itemCache.setAlternativeNames(alternativeNames);
        itemCache.setUnit(unit);
        itemCache.setProductName("digitalStorageItem");

        DigitalStorageItem item = new DigitalStorageItem();
        item.setItemCache(itemCache);
        item.setQuantityCurrent(1000d);
        List<DigitalStorageItem> items = new ArrayList<>();
        items.add(item);
        return items;
    }

    private List<DigitalStorageItem> getMockedItemsWithoutMatching() {



        Unit subUnit = new Unit();
        subUnit.setName("g");

        Unit unit = new Unit();
        unit.setName("kg");
        unit.setSubUnit(Set.of(subUnit));
        unit.setConvertFactor(1000L);

        ItemCache itemCache = new ItemCache();
        itemCache.setAlternativeNames(new ArrayList<>());
        itemCache.setUnit(unit);
        itemCache.setProductName("digitalStorageItem");

        DigitalStorageItem item = new DigitalStorageItem();
        item.setItemCache(itemCache);
        item.setQuantityCurrent(1000d);
        List<DigitalStorageItem> items = new ArrayList<>();
        items.add(item);
        return items;
    }

    private RecipeDetailDto getRecipeDetailDtoWithoutUnitsAndSteps() {
        RecipeDetailDto recipeDetailDto = RecipeDetailDtoBuilder.builder()
            .id(1L)
            .title("Pasta Carbonara")
            .servings(4)
            .readyInMinutes(25)
            .summary("Classic Italian pasta dish with eggs, cheese, pancetta, and black pepper.")
            .extendedIngredients(List.of(
                RecipeIngredientDtoBuilder.builder()
                    .id(4L)
                    .name("Spaghetti")
                    .unit("g")
                    .unitEnum(UnitDtoBuilder.builder().name("g").build())
                    .amount(400.0)
                    .matched(true)
                    .autoMatched(false)
                    .realName("Spaghetti")
                    .matchedItem(null)
                    .build(),
                RecipeIngredientDtoBuilder.builder()
                    .id(5L)
                    .name("Pancetta")
                    .unit("g")
                    .unitEnum(UnitDtoBuilder.builder().name("g").build())
                    .amount(150.0)
                    .matched(true)
                    .autoMatched(false)
                    .realName("Pancetta")
                    .matchedItem(null)
                    .build(),
                RecipeIngredientDtoBuilder.builder()
                    .id(6L)
                    .name("Eggs")
                    .unit("pcs")
                    .unitEnum(UnitDtoBuilder.builder().name("unit").build())
                    .amount(3.0)
                    .matched(true)
                    .autoMatched(false)
                    .realName("Eggs")
                    .matchedItem(null)
                    .build(),
                RecipeIngredientDtoBuilder.builder()
                    .id(7L)
                    .name("Parmesan cheese")
                    .unit("g")
                    .unitEnum(UnitDtoBuilder.builder().name("g").build())
                    .amount(100.0)
                    .matched(true)
                    .autoMatched(false)
                    .realName("Parmesan cheese")
                    .matchedItem(null)
                    .build()
            ))
            .build();
        return recipeDetailDto;
    }

    private List<CookingSteps> getCookingStepsDto() {
        List<CookingSteps> steps = new LinkedList<>();
        CookingSteps cookingSteps = CookingStepsBuilder.builder()
            .steps(List.of(
                StepBuilder.builder().number(1).step("Step 1: Boil water and cook spaghetti.").build(),
                StepBuilder.builder().number(2).step("Step 2: Fry pancetta until crispy.").build(),
                StepBuilder.builder().number(3).step("Step 3: Beat eggs and mix with Parmesan cheese.").build(),
                StepBuilder.builder().number(4).step("Step 4: Combine everything and serve.").build()
            ))
            .build();
        steps.add(cookingSteps);
        return steps;
    }

    private RecipeDetailDto getExpectedRecipeDetailDtoWithUnitsAndSteps(){


        UnitDto gUnit = UnitDtoBuilder.builder()
            .name("g")
            .subUnit(new HashSet<>())
            .build();


        UnitDto pcsUnit = UnitDtoBuilder.builder()
            .name("pcs")
            .subUnit(new HashSet<>())
            .build();


        RecipeDetailDto recipeDetailDto = RecipeDetailDtoBuilder.builder()
            .id(1L)
            .title("Pasta Carbonara")
            .servings(4)
            .readyInMinutes(25)
            .summary("Classic Italian pasta dish with eggs, cheese, pancetta, and black pepper.")
            .extendedIngredients(List.of(
                RecipeIngredientDtoBuilder.builder()
                    .id(4L)
                    .name("Spaghetti")
                    .unit("g")
                    .unitEnum(UnitDtoBuilder.builder().name("g").build())
                    .amount(400.0)
                    .matched(true)
                    .autoMatched(false)
                    .realName("Spaghetti")
                    .matchedItem(null)
                    .unitEnum(gUnit)
                    .build(),
                RecipeIngredientDtoBuilder.builder()
                    .id(5L)
                    .name("Pancetta")
                    .unit("g")
                    .unitEnum(UnitDtoBuilder.builder().name("g").build())
                    .amount(150.0)
                    .matched(true)
                    .autoMatched(false)
                    .realName("Pancetta")
                    .matchedItem(null)
                    .unitEnum(gUnit)
                    .build(),
                RecipeIngredientDtoBuilder.builder()
                    .id(6L)
                    .name("Eggs")
                    .unit("pcs")
                    .unitEnum(UnitDtoBuilder.builder().name("unit").build())
                    .amount(3.0)
                    .matched(true)
                    .autoMatched(false)
                    .realName("Eggs")
                    .matchedItem(null)
                    .unitEnum(pcsUnit)
                    .build(),
                RecipeIngredientDtoBuilder.builder()
                    .id(7L)
                    .name("Parmesan cheese")
                    .unit("g")
                    .unitEnum(UnitDtoBuilder.builder().name("g").build())
                    .amount(100.0)
                    .matched(true)
                    .autoMatched(false)
                    .realName("Parmesan cheese")
                    .matchedItem(null)
                    .unitEnum(gUnit)
                    .build()
            ))
            .steps(CookingStepsBuilder.builder()
                .steps(List.of(
                    StepBuilder.builder().number(1).step("Step 1: Boil water and cook spaghetti.").build(),
                    StepBuilder.builder().number(2).step("Step 2: Fry pancetta until crispy.").build(),
                    StepBuilder.builder().number(3).step("Step 3: Beat eggs and mix with Parmesan cheese.").build(),
                    StepBuilder.builder().number(4).step("Step 4: Combine everything and serve.").build()
                ))
                .build())
            .build();

        return recipeDetailDto;
    }

}
