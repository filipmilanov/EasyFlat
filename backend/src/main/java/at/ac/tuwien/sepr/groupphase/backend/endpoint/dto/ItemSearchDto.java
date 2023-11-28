package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.ItemOrderType;

import java.time.LocalDate;

public record ItemSearchDto(
    Long itemId,
    Boolean alwaysInStock,
    String productName,
    String brand,
    String fillLevel,
    LocalDate expireDateStart,
    LocalDate expireDateEnd,
    ItemOrderType orderType

) {
}
