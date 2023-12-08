package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record ItemFromApiDto(
    String eanCode,
    String generalName,
    String productName,
    String brand,
    Long quantityTotal,
    String unit,
    String description,
    String boughtAt,
    Long status,
    String statusText
) {
}
