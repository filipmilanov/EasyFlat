package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDate;

public class ItemListDto {

    private String generalName;
    private Long quantityCurrent;

    private Long storId;


    public ItemListDto setGeneralName(String generalName) {
        this.generalName = generalName;
        return this;
    }

    public ItemListDto setQuantityCurrent(Long quantityCurrent) {
        this.quantityCurrent = quantityCurrent;
        return this;
    }

    public Long getQuantityCurrent() {
        return quantityCurrent;
    }

    public String getGeneralName() {
        return generalName;
    }

    public ItemListDto setStorId(Long storId) {
        this.storId = storId;
        return this;
    }

    public Long getStorId() {
        return storId;
    }
}

