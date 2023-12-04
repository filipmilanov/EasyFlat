package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.ItemOrderType;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.time.LocalDate;

@RecordBuilder
public record ItemSearchDto(
    Long itemId,
    Boolean alwaysInStock,
    String productName,
    String fillLevel,
    ItemOrderType orderType

) {
}
