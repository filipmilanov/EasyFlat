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
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
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
import java.util.Optional;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.g;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ml;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    void givenItemIdWhenFindByIdThenItemIsReturned() throws AuthenticationException {
        // given
        Long id = 1L;

        // when
        Optional<Item> actual = service.findById(id, "Bearer test");

        // then
        assertTrue(actual.isPresent());
        assertThat(actual.get().getItemId()).isEqualTo(id);
    }

    @Test
    void givenInvalidItemIdWhenFindByIdThenNoItem() throws AuthenticationException {
        // given
        Long id = -1L;

        // when
        Optional<Item> actual = service.findById(id, "Bearer test");

        // then
        assertTrue(actual.isEmpty());
    }

    @Test
    void givenGeneralNameWhenFindByFieldsThenItemWithGeneralNameIsReturned() throws AuthenticationException {
        // given
        ItemFieldSearchDto itemFieldSearchDto = ItemFieldSearchDtoBuilder.builder()
                .generalName("Item")
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
    void givenBrandWhenFindByFieldsThenItemWithBrandIsReturned() throws AuthenticationException {
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
    void givenBoughtAtWhenFindByFieldsThenItemWithBoughtAtIsReturned() throws AuthenticationException {
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
    void givenValidItemWhenCreateThenItemIsPersistedWithId() throws ValidationException, ConflictException, AuthenticationException {
        // given
        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("Test")
            .storId(1L)
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
            .quantityCurrent(100L)
            .quantityTotal(200L)
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
        Optional<Item> persisted = service.findById(actual.getItemId(), "Bearer token");

        assertTrue(persisted.isPresent());
        assertThat(actual).isEqualTo(persisted.get());
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
        assertThat(actual.getStorage().getStorId()).isEqualTo(itemDto.digitalStorage().storId());
        assertThat(actual.getIngredientList().stream()
            .map(Ingredient::getTitle)
            .toList()
        ).isEqualTo(itemDto.ingredients().stream().map(IngredientDto::name).toList());
    }

    @Test
    void givenValidAlwaysInStockItemWhenCreateThenItemIsPersistedWithId() throws ValidationException, ConflictException, AuthenticationException {
        // given

        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("Test")
            .storId(1L)
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
            .quantityCurrent(100L)
            .quantityTotal(200L)
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
        Optional<Item> persisted = service.findById(actual.getItemId(), "bearer token");

        assertTrue(persisted.isPresent());
        assertThat(actual).isEqualTo(persisted.get());
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
        assertThat(actual.getStorage().getStorId()).isEqualTo(itemDto.digitalStorage().storId());
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
            .storId(1L)
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
            .quantityCurrent(100L)
            .quantityTotal(-200L)
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
            .storId(1L)
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
            .quantityCurrent(100L)
            .quantityTotal(200L)
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
            .storId(-999L)
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
            .quantityCurrent(100L)
            .quantityTotal(200L)
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
    void givenValidItemWhenUpdateSingleAttributeThenItemIsUpdated() throws ValidationException, ConflictException, AuthenticationException {
        // given:
        String updatedGeneralName = "General Name Updated";

        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("Test Storage")
            .storId(1L)
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
            .quantityCurrent(100L)
            .quantityTotal(200L)
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
            .quantityCurrent(100L)
            .quantityTotal(200L)
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
        Optional<Item> updatedItem = service.findById(createdItem.getItemId(), "bearer token");

        assertAll(
            () -> assertTrue(updatedItem.isPresent()),
            () -> updatedItem.ifPresent(item -> assertEquals(updatedGeneralName, updatedItem.get().getGeneralName()))
        );
    }

    @Test
    void givenInvalidItemWhenUpdateSingleAttributeThenValidationExceptionIsThrown() throws ValidationException, ConflictException, AuthenticationException {
        // given:
        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("Test Storage")
            .storId(1L)
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
            .quantityCurrent(100L)
            .quantityTotal(200L)
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
            .quantityCurrent(-100L)
            .quantityTotal(200L)
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
    void givenValidItemWhenUpdateMultipleAttributesThenItemIsUpdated() throws ValidationException, ConflictException, AuthenticationException {
        // given:
        String updatedGeneralName = "General Name Updated";
        Long updatedCurrentAmount = 150L;

        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("Test Storage")
            .storId(1L)
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
            .quantityCurrent(100L)
            .quantityTotal(200L)
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
            .quantityTotal(200L)
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
        Optional<Item> updatedItem = service.findById(createdItem.getItemId(), "bearer token");

        assertAll(
            () -> assertTrue(updatedItem.isPresent()),
            () -> updatedItem.ifPresent(item -> assertEquals(updatedGeneralName, updatedItem.get().getGeneralName())),
            () -> updatedItem.ifPresent(item -> assertEquals(updatedCurrentAmount, updatedItem.get().getQuantityCurrent()))
        );
    }

    @Test
    void givenInvalidItemWhenUpdateMultipleAttributesThenValidationExceptionIsThrown() throws ValidationException, ConflictException, AuthenticationException {
        // given:
        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("Test Storage")
            .storId(1L)
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
            .quantityCurrent(100L)
            .quantityTotal(200L)
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
            .quantityCurrent(100L)
            .quantityTotal(200L)
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
    void givenValidItemWhenDeleteThenItemIsDeleted() throws ValidationException, ConflictException, AuthenticationException {
        // given:
        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("Test Storage")
            .storId(1L)
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
            .quantityCurrent(100L)
            .quantityTotal(200L)
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
        Optional<Item> deletedItem = service.findById(createdItem.getItemId(), "bearer token");
        assertFalse(deletedItem.isPresent());
    }
}