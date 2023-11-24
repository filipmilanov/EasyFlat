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

    public void setStorId(Long storId) {
        this.storId = storId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
