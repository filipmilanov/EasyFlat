package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDate;

public record ItemSearchDto(
    Long itemId,
    boolean alwaysInStock,
    String productName,
    String brand,
    Long fillLevel,
    LocalDate expireDateStart,
    LocalDate expireDateEnd

) {
}
