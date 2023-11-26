package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDate;

public record ItemListDto(Long itemId, String productName, String brand, Long quantityCurrent, Long quantityTotal,
                          LocalDate expireDate) {
}
