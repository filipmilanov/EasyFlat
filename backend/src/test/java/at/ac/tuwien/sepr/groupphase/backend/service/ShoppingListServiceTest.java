package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.CleanDatabase;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.IngredientsDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.ItemLabelDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.SharedFlatDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.ShoppingListDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.StorageDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.LabelMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemLabel;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.LabelRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UnitRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.ShoppingListServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.interfaces.ShoppingItemValidator;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("unitTest")
public class ShoppingListServiceTest {

    @Autowired
    private ShoppingListServiceImpl shoppingListService;

    @Autowired
    private CleanDatabase cleanDatabase;

    @MockBean
    private AuthService authService;

    @MockBean
    private ShoppingItemValidator shoppingItemValidator;

    @MockBean
    private UnitService unitService;

    @MockBean
    private IngredientService ingredientService;

    @Autowired
    private StorageDataGenerator storageDataGenerator;

    @Autowired
    private IngredientsDataGenerator ingredientsDataGenerator;

    @Autowired
    private ShoppingListDataGenerator shoppingListDataGenerator;

    @Autowired
    private SharedFlatDataGenerator sharedFlatDataGenerator;

    @Autowired
    private ItemLabelDataGenerator itemLabelDataGenerator;

    @Autowired
    private ShoppingItemRepository shoppingItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private LabelMapper labelMapper;

    private final Faker faker = new Faker(new Random(24012024));

    private ShoppingItemDto validShoppingItemDto;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        cleanDatabase.truncateAllTablesAndRestartIds();
        sharedFlatDataGenerator.generateSharedFlats();
        shoppingListDataGenerator.generateShoppingLists();
        storageDataGenerator.generateDigitalStorages();
        ingredientsDataGenerator.generateIngredients();
        shoppingListDataGenerator.generateShoppingLists();
        itemLabelDataGenerator.generateItemLabels();
        Unit testUnit1 = new Unit();
        testUnit1.setName("g");
        unitRepository.save(testUnit1);
        Unit testUnit2 = new Unit();
        testUnit2.setName("kg");
        testUnit2.setConvertFactor(1000L);
        testUnit2.setSubUnit(Set.of(testUnit1));
        unitRepository.save(testUnit2);
        UnitDto testUnitDto = UnitDtoBuilder.builder()
            .name("g")
            .subUnit(Set.of())
            .build();
        WgDetailDto sharedFlatDto = new WgDetailDto();
        sharedFlatDto.setId(1L);
        sharedFlatDto.setName("Shared Flat 1");
        DigitalStorageDto storageDto = DigitalStorageDtoBuilder.builder()
            .storageId(1L)
            .title("Storage 1")
            .sharedFlat(sharedFlatDto)
            .build();
        List<IngredientDto> ingredients = new ArrayList<>();
        ingredients.add(new IngredientDto(1L, "Ingredient 1"));
        ingredients.add(new IngredientDto(2L, "Ingredient 2"));
        ingredients.add(new IngredientDto(3L, "Ingredient 3"));
        List<ItemLabel> labels = labelRepository.findAll();
        validShoppingItemDto = ShoppingItemDtoBuilder.builder()
            .ean("1234567890123")
            .generalName("fruit")
            .productName("apple")
            .brand("clever")
            .quantityCurrent(3.0)
            .quantityTotal(3.0)
            .unit(testUnitDto)
            .description("Manufactured in " + faker.country().name())
            .priceInCent(210L)
            .alwaysInStock(false)
            .boughtAt("billa")
            .digitalStorage(storageDto)
            .ingredients(ingredients)
            .shoppingList(new ShoppingListDto(1L, "Shopping List (Default)", 0))
            .labels(labelMapper.itemLabelListToItemLabelDtoList(labels))
            .build();
        ApplicationUser testUser = userRepository.save(new ApplicationUser(null, "User", "Userer", "user@email.com", "password", Boolean.FALSE, null));
        when(authService.getUserFromToken()).thenReturn(testUser);
    }

    @Test
    void createValidShoppingItemShouldSucceed() throws ValidationException, ConflictException, AuthorizationException {
        // Mock the necessary method calls
        when(unitService.findAll()).thenReturn(Collections.emptyList());
        doNothing().when(shoppingItemValidator).validateForCreate(
            eq(validShoppingItemDto),
            any(),
            any(),
            any()
        );

        // Act
        ShoppingItem result = shoppingListService.createShoppingItem(validShoppingItemDto);

        // Assert
        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(1L, result.getItemId()),
            () -> assertEquals(validShoppingItemDto.generalName(), result.getItemCache().getGeneralName()),
            () -> assertEquals(validShoppingItemDto.productName(), result.getItemCache().getProductName()),
            () -> assertEquals(validShoppingItemDto.ean(), result.getItemCache().getEan()),
            () -> assertEquals(validShoppingItemDto.brand(), result.getItemCache().getBrand()),
            () -> assertEquals(validShoppingItemDto.unit().name(), result.getItemCache().getUnit().getName()),
            () -> assertEquals(validShoppingItemDto.description(), result.getItemCache().getDescription()),
            () -> assertEquals(validShoppingItemDto.quantityTotal(), result.getItemCache().getQuantityTotal()),
            () -> assertEquals(validShoppingItemDto.quantityCurrent(), result.getQuantityCurrent()),
            () -> assertFalse(result.getAlwaysInStock()),
            () -> assertNull(result.getMinimumQuantity()),
            () -> assertEquals(validShoppingItemDto.boughtAt(), result.getBoughtAt()),
            () -> assertEquals(validShoppingItemDto.digitalStorage().storageId(), result.getDigitalStorage().getStorageId()),
            () -> assertEquals(validShoppingItemDto.ingredients().size(), result.getItemCache().getIngredientList().size()),
            () -> assertEquals(validShoppingItemDto.shoppingList().id(), result.getShoppingList().getId()),
            () -> assertEquals(validShoppingItemDto.labels().size(), result.getLabels().size()),
            () -> assertEquals(validShoppingItemDto.labels().get(0).labelColour(), result.getLabels().get(0).getLabelColour())
        );
    }

    @Test
    void updateExistingShoppingItemShouldSucceed() throws ValidationException, ConflictException, AuthorizationException {
        when(unitService.findAll()).thenReturn(Collections.emptyList());
        when(ingredientService.findIngredientsAndCreateMissing(any())).thenReturn(new ArrayList<>());
        doNothing().when(shoppingItemValidator).validateForUpdate(
            eq(validShoppingItemDto),
            any(),
            any(),
            any()
        );

        // save shopping item to database
        shoppingItemRepository.save(new ShoppingItem());

        // update saved shopping item
        UnitDto testUnitDto = UnitDtoBuilder.builder()
            .name("kg")
            .subUnit(Set.of(UnitDtoBuilder.builder().name("g").build()))
            .convertFactor(1000L)
            .build();
        validShoppingItemDto = ShoppingItemDtoBuilder.builder()
            .itemId(1L)
            .ean("0234567890123")
            .generalName("vegetable")
            .productName("cucumber")
            .brand("smart spend")
            .quantityCurrent(2.0)
            .quantityTotal(2.0)
            .unit(testUnitDto)
            .description("Manufactured in " + faker.country().name())
            .priceInCent(210L)
            .alwaysInStock(true)
            .minimumQuantity(2.0)
            .boughtAt("spar")
            .shoppingList(new ShoppingListDto(2L, "Second1", 0))
            .build();
        ShoppingItem result = shoppingListService.updateShoppingItem(validShoppingItemDto);

        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(1L, result.getItemId()),
            () -> assertEquals(validShoppingItemDto.generalName(), result.getItemCache().getGeneralName()),
            () -> assertEquals(validShoppingItemDto.productName(), result.getItemCache().getProductName()),
            () -> assertEquals(validShoppingItemDto.ean(), result.getItemCache().getEan()),
            () -> assertEquals(validShoppingItemDto.brand(), result.getItemCache().getBrand()),
            () -> assertEquals(validShoppingItemDto.unit().name(), result.getItemCache().getUnit().getName()),
            () -> {
                assert validShoppingItemDto.unit().subUnit() != null;
                assertEquals(validShoppingItemDto.unit().subUnit().size(), result.getItemCache().getUnit().getSubUnit().size());
            },
            () -> assertEquals(validShoppingItemDto.unit().convertFactor(), result.getItemCache().getUnit().getConvertFactor()),
            () -> assertEquals(validShoppingItemDto.description(), result.getItemCache().getDescription()),
            () -> assertEquals(validShoppingItemDto.quantityTotal(), result.getItemCache().getQuantityTotal()),
            () -> assertEquals(validShoppingItemDto.quantityCurrent(), result.getQuantityCurrent()),
            () -> assertTrue(result.getAlwaysInStock()),
            () -> assertEquals(validShoppingItemDto.minimumQuantity(), result.getMinimumQuantity()),
            () -> assertEquals(validShoppingItemDto.boughtAt(), result.getBoughtAt()),
            // updated shopping item has no ingredients
            () -> assertEquals(0, result.getItemCache().getIngredientList().size()),
            () -> assertEquals(validShoppingItemDto.shoppingList().id(), result.getShoppingList().getId()),
            () -> assertNull(result.getLabels())
        );
    }

    @Test
    void updateNonExistingShoppingItemShouldThrowNotFoundException() throws ConflictException, ValidationException {
        when(unitService.findAll()).thenReturn(Collections.emptyList());
        when(ingredientService.findIngredientsAndCreateMissing(any())).thenReturn(new ArrayList<>());
        doNothing().when(shoppingItemValidator).validateForUpdate(
            eq(validShoppingItemDto),
            any(),
            any(),
            any()
        );

        ShoppingItemDto nonExistingShoppingItemDto = ShoppingItemDtoBuilder.builder()
                .itemId(-1L).build();

        assertThrows(NotFoundException.class, () -> shoppingListService.updateShoppingItem(nonExistingShoppingItemDto));

    }

    @Test
    void getItemsOfNonExistingShoppingListShouldThrowNotFoundException() {
        Long idOfNonExistingShoppingList = -1L;
        assertThrows(NotFoundException.class, () -> shoppingListService.getItemsByShoppingListId(idOfNonExistingShoppingList, new ShoppingItemSearchDto(null, null, null)));
    }

    @Test
    void givenUnauthorizedUserWhenGetItemsByShoppingListIdShouldThrowAuthorizationException() {
        ApplicationUser testUser = userRepository.save(new ApplicationUser(null, "User1", "Userer1", "user1@email.com", "password", Boolean.FALSE, new SharedFlat().setId(2L)));
        Long idOfExistingShoppingList = 1L; // is linked to SharedFlat with Id 1

        when(authService.getUserFromToken()).thenReturn(testUser);


        assertThrows(AuthorizationException.class, () -> shoppingListService.getItemsByShoppingListId(idOfExistingShoppingList, new ShoppingItemSearchDto(null, null, null)));
    }

}
