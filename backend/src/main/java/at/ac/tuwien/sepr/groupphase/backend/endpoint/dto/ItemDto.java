package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDate;

public record ItemDto(
    Long itemId,
    String ean,
    String generalName,
    String productName,
    String brand,
    Long quantityCurrent,
    Long quantityTotal,
    String unit,
    LocalDate expireDate,
    String description,
    Long priceInCent
) {

    public ItemDto withId(Long id) {
        return new ItemDto(
            id,
            ean,
            generalName,
            productName,
            brand,
            quantityCurrent,
            quantityTotal,
            unit,
            expireDate,
            description,
            priceInCent
        );
    }

}
