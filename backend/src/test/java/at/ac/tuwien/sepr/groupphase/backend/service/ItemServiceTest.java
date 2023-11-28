package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import jakarta.validation.ValidationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class ItemServiceTest {

    @Autowired
    private ItemService service;

    @Test
    void givenValidItemWhenCreateThenItemIsPersistedWithId() throws ValidationException, ConflictException {
        // given
        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("Test")
            .storId(1L)
            .build();

        ItemDto itemDto = ItemDtoBuilder.builder()
            .ean("0123456789123")
            .generalName("Test")
            .productName("MyTest")
            .brand("Hofer")
            .quantityCurrent(100L)
            .quantityTotal(200L)
            .unit("ml")
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(digitalStorageDto)
            .ingredientsIdList(List.of(1L, 2L))
            .build();

        // when
        Item actual = service.create(itemDto);

        // then
        Optional<Item> persisted = service.findById(actual.getItemId());

        assertTrue(persisted.isPresent());
        Assertions.assertThat(actual).isEqualTo(persisted.get());
        Assertions.assertThat(actual)
            .extracting(
                Item::getEan,
                Item::getGeneralName,
                Item::getProductName,
                Item::getBrand,
                Item::getQuantityCurrent,
                Item::getQuantityTotal,
                Item::getUnit,
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
                itemDto.unit(),
                itemDto.expireDate(),
                itemDto.description(),
                itemDto.priceInCent()
            );
        assertThat(actual.getStorage().getStorId()).isEqualTo(itemDto.digitalStorage().storId());
        assertThat(actual.getIngredientList().stream()
            .map(Ingredient::getIngrId)
            .toList()
        ).isEqualTo(itemDto.ingredientsIdList());
    }

    @Test
    void givenValidAlwaysInStockItemWhenCreateThenItemIsPersistedWithId() throws ValidationException, ConflictException {
        // given

        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("Test")
            .storId(1L)
            .build();

        ItemDto itemDto = ItemDtoBuilder.builder()
            .ean("0123456789123")
            .generalName("Test")
            .productName("MyTest")
            .brand("Hofer")
            .quantityCurrent(100L)
            .quantityTotal(200L)
            .unit("ml")
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(digitalStorageDto)
            .ingredientsIdList(List.of(1L, 2L))
            .alwaysInStock(true)
            .minimumQuantity(10L)
            .boughtAt("Hofer")
            .build();

        // when
        Item actual = service.create(itemDto);

        // then
        Optional<Item> persisted = service.findById(actual.getItemId());

        assertTrue(persisted.isPresent());
        Assertions.assertThat(actual).isEqualTo(persisted.get());
        Assertions.assertThat(actual)
            .extracting(
                Item::getEan,
                Item::getGeneralName,
                Item::getProductName,
                Item::getBrand,
                Item::getQuantityCurrent,
                Item::getQuantityTotal,
                Item::getUnit,
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
                itemDto.unit(),
                itemDto.expireDate(),
                itemDto.description(),
                itemDto.priceInCent(),
                itemDto.alwaysInStock(),
                itemDto.minimumQuantity(),
                itemDto.boughtAt()
            );
        assertThat(actual.getStorage().getStorId()).isEqualTo(itemDto.digitalStorage().storId());
        assertThat(actual.getIngredientList().stream()
            .map(Ingredient::getIngrId)
            .toList()
        ).isEqualTo(itemDto.ingredientsIdList());
    }

    @Test
    void givenInvalidItemWhenCreateThenValidationExceptionIsThrown() {
        // given

        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("Test")
            .storId(1L)
            .build();

        ItemDto itemDto = ItemDtoBuilder.builder()
            .ean("2314")
            .generalName("")
            .productName(null)
            .brand("")
            .quantityCurrent(100L)
            .quantityTotal(-200L)
            .unit("")
            .description("")
            .priceInCent(-1234L)
            .digitalStorage(digitalStorageDto)
            .ingredientsIdList(List.of(1L, 2L))
            .boughtAt("Hofer")
            .build();

        // when + then
        String message = assertThrows(ValidationException.class, () -> service.create(itemDto)).getMessage();
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

        ItemDto itemDto = ItemDtoBuilder.builder()
            .ean("0123456789123")
            .generalName("Test")
            .productName("MyTest")
            .brand("Hofer")
            .quantityCurrent(100L)
            .quantityTotal(200L)
            .unit("ml")
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(digitalStorageDto)
            .ingredientsIdList(List.of(1L, 2L))
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
            .storId(-999L)
            .build();

        ItemDto itemDto = ItemDtoBuilder.builder()
            .ean("0123456789123")
            .generalName("Test")
            .productName("MyTest")
            .brand("Hofer")
            .quantityCurrent(100L)
            .quantityTotal(200L)
            .unit("ml")
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(digitalStorageDto)
            .ingredientsIdList(List.of(-1L, -2L))
            .build();

        // when + then
        String message = assertThrows(ConflictException.class, () -> service.create(itemDto)).getMessage();
        assertThat(message)
            .contains(
                "Storage",
                "not exists"
            );
    }
}