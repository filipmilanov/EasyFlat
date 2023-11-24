package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record DigitalStorageDto(
    Long storId,
    String title
) {

    public DigitalStorageDto withId(Long storId) {
        return new DigitalStorageDto(
            storId,
            title
        );
    }

}
