package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemFieldSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemFieldSearchDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.g;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ml;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.validInStockItemDto;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataConverter.convertToAlwaysInStockItemDto;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataConverter.updateProductName;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataConverter.updateUnit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class ItemServiceTest {

    @Autowired
    private ItemService service;

    @Autowired
    private TestDataGenerator testDataGenerator;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private AuthService authService;

    private ApplicationUser applicationUser;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();

        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);
    }

    @Test
    @DisplayName("Finding item with valid ID should return the item if it belongs to the current user")
    void givenItemIdWhenFindByIdThenItemIsReturned() throws AuthorizationException {
        // given
        Long id = 1L;

        // when
        DigitalStorageItem actual = service.findById(id);

        // then
        assertThat(actual.getItemId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Finding item with invalid ID should return a not found exception")
    void givenInvalidItemIdWhenFindByIdThenNoItem() {
        // given
        Long id = -1L;

        // when + then
        assertThrows(NotFoundException.class, () -> service.findById(id));
    }

    @Test
    @DisplayName("Find all items with a given general name")
    void givenGeneralNameWhenFindByFieldsThenItemWithGeneralNameIsReturned() {
        // given
        ItemFieldSearchDto itemFieldSearchDto = ItemFieldSearchDtoBuilder.builder()
            .generalName("Noodles")
            .build();

        // when
        List<DigitalStorageItem> actual = service.findByFields(itemFieldSearchDto);

        // then
        assertThat(actual).isNotEmpty();
        actual.forEach(item ->
            assertThat(item.getItemCache().getGeneralName()).containsSequence(itemFieldSearchDto.generalName())
        );
    }

    @Test
    @DisplayName("Searching for item brand should return all items that have this brand")
    void givenBrandWhenFindByFieldsThenItemWithBrandIsReturned() {
        // given
        ItemFieldSearchDto itemFieldSearchDto = ItemFieldSearchDtoBuilder.builder()
            .brand("alpro")
            .build();

        // when
        List<DigitalStorageItem> actual = service.findByFields(itemFieldSearchDto);

        // then
        assertThat(actual).isNotEmpty();
        actual.forEach(item ->
            assertThat(item.getItemCache().getBrand()).containsSequence(itemFieldSearchDto.brand())
        );
    }

    @Test
    @DisplayName("Searching for item store should return all items that were bought at this store")
    void givenBoughtAtWhenFindByFieldsThenItemWithBoughtAtIsReturned() {
        // given
        ItemFieldSearchDto itemFieldSearchDto = ItemFieldSearchDtoBuilder.builder()
            .boughtAt("Billa")
            .build();

        // when
        List<DigitalStorageItem> actual = service.findByFields(itemFieldSearchDto);

        // then
        assertThat(actual).isNotEmpty();
        actual.forEach(item ->
            assertThat(item.getBoughtAt()).containsSequence(itemFieldSearchDto.boughtAt())
        );
    }

    @Test
    @DisplayName("It is possible to create an in-stock item using valid values")
    void givenValidItemWhenCreateThenItemIsPersistedWithId() throws ValidationException, ConflictException, AuthorizationException {
        // given
        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("Test")
            .storageId(1L)
            .build();
        List<IngredientDto> ingredientDtoList = List.of(
            IngredientDtoBuilder.builder()
                .name("Ingredient 1")
                .build(),
            IngredientDtoBuilder.builder()
                .name("Ingredient 3")
                .build()
        );

        ItemDto itemDto = ItemDtoBuilder.builder()
            .ean("0123456789123")
            .generalName("Test")
            .productName("MyTest")
            .brand("Hofer")
            .quantityCurrent(100.0)
            .quantityTotal(200.0)
            .unit(ml)
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(digitalStorageDto)
            .ingredients(ingredientDtoList)
            .build();

        // when
        DigitalStorageItem actual = service.create(itemDto);

        // then
        DigitalStorageItem persisted = service.findById(actual.getItemId());

        assertThat(actual).isEqualTo(persisted);
        assertThat(actual)
            .extracting(
                (item) -> item.getItemCache().getEan(),
                (item) -> item.getItemCache().getGeneralName(),
                (item) -> item.getItemCache().getProductName(),
                (item) -> item.getItemCache().getBrand(),
                DigitalStorageItem::getQuantityCurrent,
                (item) -> item.getItemCache().getQuantityTotal(),
                (item) -> item.getItemCache().getUnit().getName(),
                DigitalStorageItem::getExpireDate,
                (item) -> item.getItemCache().getDescription(),
                DigitalStorageItem::getPriceInCent
            )
            .containsExactly(
                itemDto.ean(),
                itemDto.generalName(),
                itemDto.productName(),
                itemDto.brand(),
                itemDto.quantityCurrent(),
                itemDto.quantityTotal(),
                itemDto.unit().name(),
                itemDto.expireDate(),
                itemDto.description(),
                itemDto.priceInCent()
            );
        assertThat(actual.getDigitalStorage().getStorageId()).isEqualTo(itemDto.digitalStorage().storageId());
        assertThat(actual.getIngredientList().stream()
            .map(Ingredient::getTitle)
            .toList()
        ).isEqualTo(itemDto.ingredients().stream().map(IngredientDto::name).toList());
    }

    @Test
    @DisplayName("It is possible to create an always-in-stock item using valid values")
    void givenValidAlwaysInStockItemWhenCreateThenItemIsPersistedWithId() throws ValidationException, ConflictException, AuthorizationException {
        // given

        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("Test")
            .storageId(1L)
            .build();
        List<IngredientDto> ingredientDtoList = List.of(
            IngredientDtoBuilder.builder()
                .name("Ingredient 1")
                .build(),
            IngredientDtoBuilder.builder()
                .name("Ingredient 3")
                .build()
        );

        ItemDto itemDto = ItemDtoBuilder.builder()
            .ean("0123456789123")
            .generalName("Test")
            .productName("MyTest")
            .brand("Hofer")
            .quantityCurrent(100.0)
            .quantityTotal(200.0)
            .unit(ml)
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(digitalStorageDto)
            .ingredients(ingredientDtoList)
            .alwaysInStock(true)
            .minimumQuantity(10L)
            .boughtAt("Hofer")
            .build();

        // when
        DigitalStorageItem actual = service.create(itemDto);

        // then
        DigitalStorageItem persisted = service.findById(actual.getItemId());

        assertThat(actual).isEqualTo(persisted);
        assertThat(actual)
            .extracting(
                (item) -> item.getItemCache().getEan(),
                (item) -> item.getItemCache().getGeneralName(),
                (item) -> item.getItemCache().getProductName(),
                (item) -> item.getItemCache().getBrand(),
                DigitalStorageItem::getQuantityCurrent,
                (item) -> item.getItemCache().getQuantityTotal(),
                (item) -> item.getItemCache().getUnit().getName(),
                DigitalStorageItem::getExpireDate,
                (item) -> item.getItemCache().getDescription(),
                DigitalStorageItem::getPriceInCent,
                DigitalStorageItem::alwaysInStock,
                DigitalStorageItem::getMinimumQuantity,
                DigitalStorageItem::getBoughtAt
            )
            .containsExactly(
                itemDto.ean(),
                itemDto.generalName(),
                itemDto.productName(),
                itemDto.brand(),
                itemDto.quantityCurrent(),
                itemDto.quantityTotal(),
                itemDto.unit().name(),
                itemDto.expireDate(),
                itemDto.description(),
                itemDto.priceInCent(),
                itemDto.alwaysInStock(),
                itemDto.minimumQuantity(),
                itemDto.boughtAt()
            );
        assertThat(actual.getDigitalStorage().getStorageId()).isEqualTo(itemDto.digitalStorage().storageId());
        assertThat(actual.getIngredientList().stream()
            .map(Ingredient::getTitle)
            .toList()
        ).isEqualTo(itemDto.ingredients().stream().map(IngredientDto::name).toList());
    }

    @Test
    @DisplayName("It is not possible to create an in-stock item using invalid values")
    void givenInvalidItemWhenCreateThenValidationExceptionIsThrown() {
        // given

        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("Test")
            .storageId(1L)
            .build();
        List<IngredientDto> ingredientDtoList = List.of(
            IngredientDtoBuilder.builder()
                .name("Ingredient 1")
                .build(),
            IngredientDtoBuilder.builder()
                .name("Ingredient 3")
                .build()
        );

        ItemDto itemDto = ItemDtoBuilder.builder()
            .ean("2314")
            .generalName("")
            .productName(null)
            .brand("")
            .quantityCurrent(100.0)
            .quantityTotal(-200.0)
            .unit(UnitDtoBuilder.builder().build())
            .description("")
            .priceInCent(-1234L)
            .digitalStorage(digitalStorageDto)
            .ingredients(ingredientDtoList)
            .boughtAt("Hofer")
            .build();

        // when + then
        String message = assertThrows(ValidationException.class, () -> service.create(itemDto)).getMessage();
        assertThat(message)
            .contains(
                "quantity",
                "category",
                "product name",
                "EAN",
                "13",
                "price",
                "total"
            );
    }

    @Test
    @DisplayName("It is not possible to create an always-in-stock item using invalid values")
    void givenInvalidAlwaysInStockItemWhenCreateThenValidationExceptionIsThrown() {
        // given
        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("Test")
            .storageId(1L)
            .build();
        List<IngredientDto> ingredientDtoList = List.of(
            IngredientDtoBuilder.builder()
                .name("Ingredient 1")
                .build(),
            IngredientDtoBuilder.builder()
                .name("Ingredient 3")
                .build()
        );

        ItemDto itemDto = ItemDtoBuilder.builder()
            .ean("0123456789123")
            .generalName("Test")
            .productName("MyTest")
            .brand("Hofer")
            .quantityCurrent(100.0)
            .quantityTotal(200.0)
            .unit(ml)
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(digitalStorageDto)
            .ingredients(ingredientDtoList)
            .alwaysInStock(true)
            .boughtAt("Hofer")
            .build();

        // when + then
        String message = assertThrows(ValidationException.class, () -> service.create(itemDto)).getMessage();
        assertThat(message)
            .contains(
                "minimum quantity"
            );
    }

    @Test
    @DisplayName("It is not possible to create an item for an invalid storage")
    void givenItemWithInvalidStorageWhenCreateThenConflictExceptionIsThrown() {
        // given
        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("Test")
            .storageId(-999L)
            .build();
        List<IngredientDto> ingredientDtoList = List.of(
            IngredientDtoBuilder.builder()
                .name("Ingredient 1")
                .build(),
            IngredientDtoBuilder.builder()
                .name("Ingredient 3")
                .build()
        );

        ItemDto itemDto = ItemDtoBuilder.builder()
            .ean("0123456789123")
            .generalName("Test")
            .productName("MyTest")
            .brand("Hofer")
            .quantityCurrent(100.0)
            .quantityTotal(200.0)
            .unit(ml)
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(digitalStorageDto)
            .ingredients(ingredientDtoList)
            .build();

        // when + then
        String message = assertThrows(ConflictException.class, () -> service.create(itemDto)).getMessage();
        assertThat(message).isNotEmpty();
        assertThat(message)
            .contains(
                "Digital Storage",
                "not exists"
            );
    }

    @Test
    @DisplayName("It is possible to update an item using a valid value")
    void givenValidItemWhenUpdateSingleAttributeThenItemIsUpdated() throws ValidationException, ConflictException, AuthorizationException {
        // given:
        String updatedGeneralName = "General Name Updated";

        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("Test Storage")
            .storageId(1L)
            .build();

        List<IngredientDto> ingredientDtoList = List.of(
            IngredientDtoBuilder.builder()
                .name("Test Ingredient 1")
                .build(),
            IngredientDtoBuilder.builder()
                .name("Test Ingredient 2")
                .build()
        );

        ItemDto itemDto = ItemDtoBuilder.builder()
            .ean("0123456789123")
            .generalName("TestGeneral")
            .productName("TestProduct")
            .brand("TestBrand")
            .quantityCurrent(100.0)
            .quantityTotal(200.0)
            .unit(g)
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(digitalStorageDto)
            .ingredients(ingredientDtoList)
            .build();

        DigitalStorageItem createdDigitalStorageItem = service.create(itemDto);

        ItemDto updatedItemDto = ItemDtoBuilder.builder()
            .itemId(createdDigitalStorageItem.getItemId())
            .ean("0123456789123")
            .generalName(updatedGeneralName)
            .productName("TestProduct")
            .brand("TestBrand")
            .quantityCurrent(100.0)
            .quantityTotal(200.0)
            .unit(g)
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(digitalStorageDto)
            .ingredients(ingredientDtoList)
            .build();

        // when:
        service.update(updatedItemDto);

        // then:
        DigitalStorageItem updatedItem = service.findById(createdDigitalStorageItem.getItemId());

        assertEquals(updatedGeneralName, updatedItem.getItemCache().getGeneralName());

    }

    @Test
    @DisplayName("It is not possible to update an item using an invalid value")
    void givenInvalidItemWhenUpdateSingleAttributeThenValidationExceptionIsThrown() throws ValidationException, ConflictException, AuthorizationException {
        // given:
        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("Test Storage")
            .storageId(1L)
            .build();

        List<IngredientDto> ingredientDtoList = List.of(
            IngredientDtoBuilder.builder()
                .name("Test Ingredient 1")
                .build(),
            IngredientDtoBuilder.builder()
                .name("Test Ingredient 2")
                .build()
        );

        ItemDto itemDto = ItemDtoBuilder.builder()
            .ean("0123456789123")
            .generalName("TestGeneral")
            .productName("TestProduct")
            .brand("TestBrand")
            .quantityCurrent(100.0)
            .quantityTotal(200.0)
            .unit(g)
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(digitalStorageDto)
            .ingredients(ingredientDtoList)
            .build();

        DigitalStorageItem createdDigitalStorageItem = service.create(itemDto);

        ItemDto updatedItemDto = ItemDtoBuilder.builder()
            .itemId(createdDigitalStorageItem.getItemId())
            .ean("0123456789123")
            .generalName("TestGeneral")
            .productName("TestProduct")
            .brand("TestBrand")
            .quantityCurrent(-100.0)
            .quantityTotal(200.0)
            .unit(g)
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(digitalStorageDto)
            .ingredients(ingredientDtoList)
            .build();

        // when + then
        String message = assertThrows(ValidationException.class, () -> service.update(updatedItemDto)).getMessage();
        assertThat(message)
            .contains(
                "current quantity",
                "0"
            );
    }

    @Test
    @DisplayName("It is possible to update multiple fields of an item using valid values")
    void givenValidItemWhenUpdateMultipleAttributesThenItemIsUpdated() throws ValidationException, ConflictException, AuthorizationException {
        // given:
        String updatedGeneralName = "General Name Updated";
        Double updatedCurrentAmount = 150.0;

        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("Test Storage")
            .storageId(1L)
            .build();

        List<IngredientDto> ingredientDtoList = List.of(
            IngredientDtoBuilder.builder()
                .name("Test Ingredient 1")
                .build(),
            IngredientDtoBuilder.builder()
                .name("Test Ingredient 2")
                .build()
        );

        ItemDto itemDto = ItemDtoBuilder.builder()
            .ean("0123456789123")
            .generalName("TestGeneral")
            .productName("TestProduct")
            .brand("TestBrand")
            .quantityCurrent(100.0)
            .quantityTotal(200.0)
            .unit(g)
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(digitalStorageDto)
            .ingredients(ingredientDtoList)
            .build();

        DigitalStorageItem createdDigitalStorageItem = service.create(itemDto);

        ItemDto updatedItemDto = ItemDtoBuilder.builder()
            .itemId(createdDigitalStorageItem.getItemId())
            .ean("0123456789123")
            .generalName(updatedGeneralName)
            .productName("TestProduct")
            .brand("TestBrand")
            .quantityCurrent(updatedCurrentAmount)
            .quantityTotal(200.0)
            .unit(g)
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(digitalStorageDto)
            .ingredients(ingredientDtoList)
            .build();

        // when:
        service.update(updatedItemDto);

        // then:
        DigitalStorageItem updatedItem = service.findById(createdDigitalStorageItem.getItemId());

        assertAll(
            () -> assertEquals(updatedGeneralName, updatedItem.getItemCache().getGeneralName()),
            () -> assertEquals(updatedCurrentAmount, updatedItem.getQuantityCurrent())
        );
    }

    @Test
    @DisplayName("It is not possible to update multiple fields of an item using invalid values")
    void givenInvalidItemWhenUpdateMultipleAttributesThenValidationExceptionIsThrown() throws ValidationException, ConflictException, AuthorizationException {
        // given:
        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("Test Storage")
            .storageId(1L)
            .build();

        List<IngredientDto> ingredientDtoList = List.of(
            IngredientDtoBuilder.builder()
                .name("Test Ingredient 1")
                .build(),
            IngredientDtoBuilder.builder()
                .name("Test Ingredient 2")
                .build()
        );

        ItemDto itemDto = ItemDtoBuilder.builder()
            .ean("0123456789123")
            .generalName("TestGeneral")
            .productName("TestProduct")
            .brand("TestBrand")
            .quantityCurrent(100.0)
            .quantityTotal(200.0)
            .unit(g)
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(digitalStorageDto)
            .ingredients(ingredientDtoList)
            .build();

        DigitalStorageItem createdDigitalStorageItem = service.create(itemDto);

        ItemDto updatedItemDto = ItemDtoBuilder.builder()
            .itemId(createdDigitalStorageItem.getItemId())
            .ean("0123456789123")
            .generalName("TestGeneral")
            .productName("TestProduct")
            .brand(null)
            .quantityCurrent(100.0)
            .quantityTotal(200.0)
            .unit(g)
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(null)
            .ingredients(ingredientDtoList)
            .build();

        // when + then
        String message = assertThrows(ValidationException.class, () -> service.update(updatedItemDto)).getMessage();
        assertThat(message)
            .contains(
                "storage"
            );
    }

    @Test
    @DisplayName("It is possible to delete an item")
    void givenValidItemWhenDeleteThenItemIsDeleted() throws ValidationException, ConflictException, AuthorizationException {
        // given:
        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("Test Storage")
            .storageId(1L)
            .build();

        List<IngredientDto> ingredientDtoList = List.of(
            IngredientDtoBuilder.builder()
                .name("Test Ingredient 1")
                .build(),
            IngredientDtoBuilder.builder()
                .name("Test Ingredient 2")
                .build()
        );

        ItemDto itemDto = ItemDtoBuilder.builder()
            .ean("0123456789123")
            .generalName("TestGeneral")
            .productName("TestProduct")
            .brand("TestBrand")
            .quantityCurrent(100.0)
            .quantityTotal(200.0)
            .unit(g)
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(digitalStorageDto)
            .ingredients(ingredientDtoList)
            .build();

        DigitalStorageItem createdDigitalStorageItem = service.create(itemDto);

        // when:
        service.delete(createdDigitalStorageItem.getItemId());

        // then:
        assertThrows(NotFoundException.class, () -> service.findById(createdDigitalStorageItem.getItemId()));
    }

    @Test
    @DisplayName("Items should be retrievable using their general name")
    void givenValidSearchParamsWhenGetItemsWithGeneralNameThenReturnList() {
        // given
        String itemName = "apples";

        // when
        List<DigitalStorageItem> result = service.getItemWithGeneralName(itemName);


        // then
        assertAll(
            () -> assertThat(result).isNotEmpty(),
            () -> assertThat(result).isNotNull(),
            () -> assertEquals(result.size(), 1)
        );
    }

    @Test
    @DisplayName("Move Item from InStock to AlwaysInStock, and then the same Item back to Instock - Refs: #339")
    public void moveItemsBetweenInSockAndAlwaysInStock() throws ValidationException, AuthorizationException, ConflictException {
        // given
        DigitalStorageItem item = service.create(validInStockItemDto);

        // when + then
        ItemDto toAlwaysInStockItemDto = convertToAlwaysInStockItemDto(
            validInStockItemDto.withId(item.getItemId()),
            10L
        );

        assertDoesNotThrow(() -> {
                DigitalStorageItem updatedItem = service.update(toAlwaysInStockItemDto);
                service.update(validInStockItemDto.withId(updatedItem.getItemId()));
            }
        );
    }

    @Test
    @DisplayName("Create two itemes with same general name and different units - Refs: #253")
    public void createTwoItemsWithSameGeneralNameAndDifferentUnits() throws ValidationException, AuthorizationException, ConflictException {
        // given
        ItemDto itemDtoWithDifferentUnit = updateUnit(
            validInStockItemDto,
            UnitDtoBuilder.builder().name("g").build()
        );
        DigitalStorageItem createdDigitalStorageItem = service.create(validInStockItemDto);

        // when + then
        String errorMessage = assertThrows(ConflictException.class, () ->
            service.create(itemDtoWithDifferentUnit)
        ).getMessage();

        assertThat(errorMessage).containsSubsequence("unit");
    }

    @Test
    @DisplayName("Update Item within same general name to different unit - Refs: #253")
    public void updateItemWithSameGeneralNameToDifferentUnit() throws ValidationException, AuthorizationException, ConflictException {
        // given
        DigitalStorageItem createdDigitalStorageItem = service.create(validInStockItemDto);
        DigitalStorageItem createdDigitalStorageItem2 = service.create(updateProductName(validInStockItemDto, "Test2"));

        ItemDto itemDtoWithDifferentUnit = updateUnit(
            validInStockItemDto.withId(createdDigitalStorageItem.getItemId()),
            UnitDtoBuilder.builder().name("g").build()
        );

        // when + then
        String errorMessage = assertThrows(ConflictException.class, () ->
            service.update(itemDtoWithDifferentUnit)
        ).getMessage();

        assertThat(errorMessage).containsSubsequence("unit");
    }
}