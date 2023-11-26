package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import jakarta.xml.bind.ValidationException;
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
            .storageId(-1L)
            .ingredientsIdList(List.of(-1L, -2L))
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
                Item::getStorage,
                Item::getIngredientList
            )
            .containsExactly(
                itemDto.ean(),
                itemDto.generalName(),
                itemDto.priceInCent(),
                itemDto.brand(),
                itemDto.quantityCurrent(),
                itemDto.quantityTotal(),
                itemDto.unit(),
                itemDto.expireDate(),
                itemDto.description(),
                itemDto.priceInCent(),
                itemDto.storageId(),
                itemDto.ingredientsIdList()
            );
    }

    @Test
    void givenInvalidItemWhenCreateThenValidationExceptionIsThrown() {
        // given
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
            .storageId(-1L)
            .ingredientsIdList(List.of(-1L, -2L))
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
    void givenItemWithInvalidStorageWhenCreateThenConflictExceptionIsThrown() {
        // given
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
            .storageId(-999L)
            .ingredientsIdList(List.of(-1L, -2L))
            .build();

        // when + then
        String message = assertThrows(ValidationException.class, () -> service.create(itemDto)).getMessage();
        assertThat(message)
            .contains(
                "storage",
                "not found"
            );
    }

    @Test
    void givenItemWithInvalidIngredientsWhenCreateThenConflictExceptionIsThrown() {
        // given
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
            .storageId(-999L)
            .ingredientsIdList(List.of(-1L, -2L))
            .build();

        // when + then
        String message = assertThrows(ConflictException.class, () -> service.create(itemDto)).getMessage();
        assertThat(message)
            .contains(
                "storage",
                "not found"
            );
    }
}