package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.time.LocalDate;
import java.util.List;

@RecordBuilder
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
    Long priceInCent,
    Boolean alwaysInStock,
    Long minimumQuantity,
    Long storageId,
    List<Long> ingredientsIdList
) {
    public ItemDto withAlwaysInStock(Boolean alwaysInStock) {
        return new ItemDto(
            itemId,
            ean,
            generalName,
            productName,
            brand,
            quantityCurrent,
            quantityTotal,
            unit,
            expireDate,
            description,
            priceInCent,
            alwaysInStock,
            minimumQuantity,
            storageId,
            ingredientsIdList
        );
    }


}