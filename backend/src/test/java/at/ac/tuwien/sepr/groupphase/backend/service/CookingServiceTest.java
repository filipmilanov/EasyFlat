package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeIngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeIngredientDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SharedFlatMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeSuggestion;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import com.deepl.api.DeepLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
    private JwtTokenizer jwtTokenizer;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UnitService unitService;

    private ApplicationUser applicationUser;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();
        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);

    }

    @Test
    @Disabled
    void testGetRecipeSuggestion() throws ValidationException, ConflictException, AuthenticationException, AuthorizationException, DeepLException, InterruptedException {

        when(jwtTokenizer.getEmailFromToken(any(String.class))).thenReturn(applicationUser.getEmail());

        // when
        List<RecipeSuggestionDto> result = cookingService.getRecipeSuggestion("");

        // then
        assertThat(result)
            .isNotEmpty()
            .extracting("title", "servings", "readyInMinutes")
            .contains(
                tuple("Napoleon - A Creamy Puff Pastry Cake", 9, 45),
                tuple("Baked Custard", 6, 45)
            );

    }


    @Test
    @Disabled
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
    @Disabled
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

        ItemSearchDto searchParamsIS = new ItemSearchDto(null, null, null, null);
        ItemSearchDto searchParamsAIS = new ItemSearchDto(null, null, null, null);
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
                if(item.generalName().equals(initialItem.generalName())) {
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

}
