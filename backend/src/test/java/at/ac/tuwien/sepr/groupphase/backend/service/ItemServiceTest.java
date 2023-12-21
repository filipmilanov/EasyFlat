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
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
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
import static org.mockito.ArgumentMatchers.any;
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
    private CustomUserDetailService customUserDetailService;

    private ApplicationUser applicationUser;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();

        applicationUser = userRepository.findById(1L).orElseThrow();
        when(customUserDetailService.getUser(any(String.class))).thenReturn(applicationUser);
    }


    @Test
    void givenItemIdWhenFindByIdThenItemIsReturned() throws AuthorizationException {
        // given
        Long id = 1L;

        // when
        Item actual = service.findById(id, "Bearer test");

        // then
        assertThat(actual.getItemId()).isEqualTo(id);
    }

    @Test
    void givenInvalidItemIdWhenFindByIdThenNoItem() throws AuthorizationException {
        // given
        Long id = -1L;

        // when + them
        assertThrows(NotFoundException.class, () -> service.findById(id, "Bearer test"));
    }

    @Test
    void givenGeneralNameWhenFindByFieldsThenItemWithGeneralNameIsReturned() throws AuthorizationException {
        // given
        ItemFieldSearchDto itemFieldSearchDto = ItemFieldSearchDtoBuilder.builder()
                .generalName("apples")
                .build();

        // when
        List<Item> actual = service.findByFields(itemFieldSearchDto);

        // then
        assertThat(actual).isNotEmpty();
        actual.forEach(item ->
                assertThat(item.getGeneralName()).containsSequence(itemFieldSearchDto.generalName())
        );
    }

    @Test
    void givenBrandWhenFindByFieldsThenItemWithBrandIsReturned() throws AuthorizationException {
        // given
        ItemFieldSearchDto itemFieldSearchDto = ItemFieldSearchDtoBuilder.builder()
                .brand("Brand")
                .build();

        // when
        List<Item> actual = service.findByFields(itemFieldSearchDto);

        // then
        assertThat(actual).isNotEmpty();
        actual.forEach(item ->
                assertThat(item.getBrand()).containsSequence(itemFieldSearchDto.brand())
        );
    }

    @Test
    void givenBoughtAtWhenFindByFieldsThenItemWithBoughtAtIsReturned() throws AuthorizationException {
        // given
        ItemFieldSearchDto itemFieldSearchDto = ItemFieldSearchDtoBuilder.builder()
                .boughtAt("Hofer")
                .build();

        // when
        List<Item> actual = service.findByFields(itemFieldSearchDto);

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
        Item actual = service.create(itemDto, "Bearer test");

        // then
        Item persisted = service.findById(actual.getItemId(), "Bearer token");

        assertThat(actual).isEqualTo(persisted);
        assertThat(actual)
            .extracting(
                Item::getEan,
                Item::getGeneralName,
                Item::getProductName,
                Item::getBrand,
                Item::getQuantityCurrent,
                Item::getQuantityTotal,
                (item) -> item.getUnit().getName(),
                Item::getExpireDate,
                Item::getDescription,
                Item::getPriceInCent
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
        assertThat(actual.getStorage().getStorageId()).isEqualTo(itemDto.digitalStorage().storageId());
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
        Item actual = service.create(itemDto, "Bearer test");

        // then
        Item persisted = service.findById(actual.getItemId(), "bearer token");

        assertThat(actual).isEqualTo(persisted);
        assertThat(actual)
            .extracting(
                Item::getEan,
                Item::getGeneralName,
                Item::getProductName,
                Item::getBrand,
                Item::getQuantityCurrent,
                Item::getQuantityTotal,
                (item) -> item.getUnit().getName(),
                Item::getExpireDate,
                Item::getDescription,
                Item::getPriceInCent,
                Item::alwaysInStock,
                Item::getMinimumQuantity,
                Item::getBoughtAt
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
        assertThat(actual.getStorage().getStorageId()).isEqualTo(itemDto.digitalStorage().storageId());
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
        String message = assertThrows(ValidationException.class, () -> service.create(itemDto, "Bearer Token")).getMessage();
        assertThat(message)
            .contains(
                "EAN",
                "13",
                "brand",
                "quantity"
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
        String message = assertThrows(ValidationException.class, () -> service.create(itemDto, "Bearer Token")).getMessage();
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
        String message = assertThrows(ConflictException.class, () -> service.create(itemDto, "Bearer Token")).getMessage();
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

        Item createdItem = service.create(itemDto, "Bearer test");

        ItemDto updatedItemDto = ItemDtoBuilder.builder()
            .itemId(createdItem.getItemId())
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
        service.update(updatedItemDto, "Bearer test");

        // then:
        Item updatedItem = service.findById(createdItem.getItemId(), "bearer token");

        assertEquals(updatedGeneralName, updatedItem.getGeneralName());
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

        Item createdItem = service.create(itemDto, "Bearer test");

        ItemDto updatedItemDto = ItemDtoBuilder.builder()
            .itemId(createdItem.getItemId())
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
        String message = assertThrows(ValidationException.class, () -> service.update(updatedItemDto, "bearer token")).getMessage();
        assertThat(message)
            .contains(
                "The actual quantity must be positive"
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

        Item createdItem = service.create(itemDto, "Bearer test");

        ItemDto updatedItemDto = ItemDtoBuilder.builder()
            .itemId(createdItem.getItemId())
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
        service.update(updatedItemDto, "Bearer test");

        // then:
        Item updatedItem = service.findById(createdItem.getItemId(), "bearer token");

        assertAll(
            () -> assertEquals(updatedGeneralName, updatedItem.getGeneralName()),
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

        Item createdItem = service.create(itemDto, "Bearer test");

        ItemDto updatedItemDto = ItemDtoBuilder.builder()
            .itemId(createdItem.getItemId())
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
        String message = assertThrows(ValidationException.class, () -> service.update(updatedItemDto, "bearer token")).getMessage();
        assertThat(message)
            .contains(
                "brand",
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

        Item createdItem = service.create(itemDto, "Bearer test");

        // when:
        service.delete(createdItem.getItemId(), "Bearer test");

        // then:
        assertThrows(NotFoundException.class, () -> service.findById(createdItem.getItemId(), "bearer token"));

    }

    @Test
    void givenValidSearchParamsWhenGetItemsWithGeneralNameThenReturnList() throws ValidationException, AuthorizationException, ConflictException {
        // given
        String itemName = "apples";
        String jwt = "Bearer Token";


        // when
        List<Item> result = service.getItemWithGeneralName(itemName, jwt);


        // then
        assertAll(
            () -> assertThat(result).isNotEmpty(),
            () -> assertThat(result).isNotNull(),
            () -> assertEquals(result.size(), 1)
        );
    }
}