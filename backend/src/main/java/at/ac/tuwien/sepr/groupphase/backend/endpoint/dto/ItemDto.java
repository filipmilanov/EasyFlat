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
}