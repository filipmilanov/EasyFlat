package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ItemLabel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shopLabelId;

    @Column
    private String labelValue;

    @Column
    private String labelColour;


    public Long getShopLabelId() {
        return shopLabelId;
    }

    public String getLabelColour() {
        return labelColour;
    }

    public String getLabelValue() {
        return labelValue;
    }

    public ItemLabel setLabelColour(String labelColour) {
        this.labelColour = labelColour;
        return this;
    }

    public ItemLabel setLabelValue(String labelValue) {
        this.labelValue = labelValue;
        return this;
    }

    public ItemLabel setShopLabelId(Long shopLabelId) {
        this.shopLabelId = shopLabelId;
        return this;
    }
}
