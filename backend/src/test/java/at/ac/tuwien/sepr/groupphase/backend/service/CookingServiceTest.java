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
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cookbook;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
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
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UnitService unitService;

    private ApplicationUser applicationUser;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();
        applicationUser = userRepository.findById(1L).orElseThrow();
        when(customUserDetailService.getUser(any(String.class))).thenReturn(applicationUser);
    }

    @Test
    @Disabled
    void testGetRecipeSuggestion() throws ValidationException, ConflictException, AuthenticationException, AuthorizationException {

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


}
