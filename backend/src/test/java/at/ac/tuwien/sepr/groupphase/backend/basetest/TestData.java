package at.ac.tuwien.sepr.groupphase.backend.basetest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;

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
        null, // Labels
        new ShoppingListDto(1L, "Default", new ArrayList<>()));

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
        null,
        null);

    ApplicationUser testUser = new ApplicationUser(null, "", "", "user@email.com", "password", Boolean.FALSE, null);

    DigitalStorageDto validDigitalStorageDto = new DigitalStorageDto(1L, "Storage", null);
    ItemDto validInStockItemDto = ItemDtoBuilder.builder()
        .ean("1234567890123")
        .generalName("Milk")
        .productName("Soja Milk")
        .alwaysInStock(false)
        .quantityCurrent(100.0)
        .quantityTotal(100.0)
        .unit(ml)
        .description("Soja Milk of a super brand")
        .brand("Super Brand")
        .digitalStorage(validDigitalStorageDto)
        .build();


}
