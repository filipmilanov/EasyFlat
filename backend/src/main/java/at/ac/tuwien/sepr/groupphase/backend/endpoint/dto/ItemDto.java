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
    Long storageId,
    List<Long> ingredientsIdList
) {


}