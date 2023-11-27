package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;


import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record DigitalStorageDto(Long storId, String title) {
}
