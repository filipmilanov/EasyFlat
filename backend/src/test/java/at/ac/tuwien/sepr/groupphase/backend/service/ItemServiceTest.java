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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.g;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ml;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
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
    void givenItemIdWhenFindByIdThenItemIsReturned() throws AuthorizationException {
        // given
        Long id = 1L;

        // when
        DigitalStorageItem actual = service.findById(id);

        // then
        assertThat(actual.getItemId()).isEqualTo(id);
    }

    @Test
    void givenInvalidItemIdWhenFindByIdThenNoItem() {
        // given
        Long id = -1L;

        // when + then
        assertThrows(NotFoundException.class, () -> service.findById(id));
    }

    @Test
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
}