package at.ac.tuwien.sepr.groupphase.backend.basetest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface TestData {

    Long ID = 1L;
    String TEST_NEWS_TITLE = "Title";
    String TEST_NEWS_SUMMARY = "Summary";
    String TEST_NEWS_TEXT = "TestMessageText";
    LocalDateTime TEST_NEWS_PUBLISHED_AT =
        LocalDateTime.of(2019, 11, 13, 12, 15, 0, 0);

    String BASE_URI = "/api/v1";
    String MESSAGE_BASE_URI = BASE_URI + "/messages";

    String ADMIN_USER = "admin@email.com";
    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }
    };
    String DEFAULT_USER = "admin@email.com";
    List<String> USER_ROLES = new ArrayList<>() {
        {
            add("ROLE_USER");
        }
    };

    UnitDto g = new UnitDto("g", null, null);
    UnitDto kg = new UnitDto("kg", 1000L, Set.of(g));

    UnitDto ml = new UnitDto("ml", null, Set.of());
    UnitDto l = new UnitDto("l", 1000L, Set.of(ml));

    ShoppingItemDto validShoppingItemDto = new ShoppingItemDto(
        null,
        null,
        "pear",
        "pear1",
        null,
        10.0,
        20.0,
        g,
        null,
        null,
        false,
        null,
        null,
        new DigitalStorageDto(1L, "Storage", null),
        null,
        null,
        new ShoppingListDto(1L, "Default", 0));

    ShoppingItemDto invalidShoppingItemDto = new ShoppingItemDto(
        null,
        "1234567890123",
        "pear",
        "pear1",
        "billa",
        10.0,
        20.0,
        new UnitDto("z", 1000L, null),
        "Description",
        500L,
        true,
        5.0,
        "Store",
        new DigitalStorageDto(1L, "Storage", null),
        null,
        null,
        null);

    ApplicationUser testUser = new ApplicationUser(null, "", "", "user@email.com", "password", Boolean.FALSE, null);

    DigitalStorageDto validDigitalStorageDto = new DigitalStorageDto(1L, "Storage", null);
    ItemDto validInStockItemDto = ItemDtoBuilder.builder()
        .ean("1234567890123")
        .generalName("MilkForTest")
        .productName("Soja Milk")
        .alwaysInStock(false)
        .quantityCurrent(100.0)
        .quantityTotal(100.0)
        .unit(ml)
        .description("Soja Milk of a super brand")
        .brand("Super Brand")
        .digitalStorage(validDigitalStorageDto)
        .build();

    ItemDto validInStockItemDto2 = ItemDtoBuilder.builder()
        .ean("1234567890124")
        .generalName("MilkForTest2")
        .productName("Soja Milk2")
        .alwaysInStock(false)
        .quantityCurrent(101.0)
        .quantityTotal(101.0)
        .unit(ml)
        .description("Soja Milk of a super brand")
        .brand("Super Brand")
        .digitalStorage(validDigitalStorageDto)
        .build();

    ItemDto validInStockItemDto3 = ItemDtoBuilder.builder()
        .ean("1234567890125")
        .generalName("Bred")
        .productName("Bred from Stroek")
        .alwaysInStock(false)
        .quantityCurrent(500.0)
        .quantityTotal(500.0)
        .unit(g)
        .description("Bred")
        .brand("Stroek")
        .digitalStorage(validDigitalStorageDto)
        .build();

    ItemDto validInStockItemDto4 = ItemDtoBuilder.builder()
        .ean("1234567890126")
        .generalName("Bred2")
        .productName("Bred from Stroek")
        .alwaysInStock(false)
        .quantityCurrent(1.0)
        .quantityTotal(1.0)
        .unit(kg)
        .description("Bred")
        .brand("Stroek")
        .digitalStorage(validDigitalStorageDto)
        .build();

    long validExpenseId = 4L;
    long unauthorizedExpenseId = 1L;

    DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
        .title("Test")
        .storageId(1L)
        .build();
    List<IngredientDto> ingredientDtoList = List.of(
        IngredientDtoBuilder.builder()
            .name("Ingredient 1")
            .build(),
        IngredientDtoBuilder.builder()
            .name("Ingredient 2")
            .build()
    );


    ItemDto validItemDto = ItemDtoBuilder.builder()
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
        .boughtAt("Hofer")
        .digitalStorage(digitalStorageDto)
        .ingredients(ingredientDtoList)
        .build();

    ItemDto invalidItemDto = ItemDtoBuilder.builder()
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

    DigitalStorageDto invalidDigitalStorageDto = DigitalStorageDtoBuilder.builder()
        .title("Test")
        .storageId(-909L)
        .build();
    List<IngredientDto> ingredientDtoList = List.of(
        IngredientDtoBuilder.builder()
            .name("Ingredient 1")
            .build(),
        IngredientDtoBuilder.builder()
            .name("Ingredient 2")
            .build()
    );

    ItemDto itemDtoWithInvalidDigitalStorage = ItemDtoBuilder.builder()
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
        .digitalStorage(invalidDigitalStorageDto)
        .ingredients(ingredientDtoList)
        .boughtAt("Hofer")
        .build();
}
