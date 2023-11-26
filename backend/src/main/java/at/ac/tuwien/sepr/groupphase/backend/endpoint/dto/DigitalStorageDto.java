package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotEmpty;

public class DigitalStorageDto {

    private Long storId;
    @NotEmpty(message = "Title cannot be empty")
    private String title;

    public DigitalStorageDto() {
    }

    public Long getStorId() {
        return storId;
    }

    public DigitalStorageDto setStorId(Long storId) {
        this.storId = storId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public DigitalStorageDto setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public String toString() {
        return "DigitalStorageDto{"
            + "storId=" + storId
            + ", title='" + title + '\''
            + '}';
    }
}
