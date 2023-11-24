package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public class ItemDto {


    private Long itemId;

    @Pattern(regexp = "[0-9](13)", message = "EAN number has exactly 13 numbers")
    private String ean;
    private String generalName;
    @NotEmpty
    private String productName;
    @NotEmpty
    private String brand;
    @Min(value = 0, message = "The quantity must be positive")
    private Long quantityCurrent;
    @Min(value = 0, message = "The quantity must be positive")
    private Long quantityTotal;
    @NotEmpty
    private String unit;
    private LocalDate expireDate;
    private String description;
    private Long priceInCent;

    public ItemDto() {
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getGeneralName() {
        return generalName;
    }

    public void setGeneralName(String generalName) {
        this.generalName = generalName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Long getQuantityCurrent() {
        return quantityCurrent;
    }

    public void setQuantityCurrent(Long quantityCurrent) {
        this.quantityCurrent = quantityCurrent;
    }

    public Long getQuantityTotal() {
        return quantityTotal;
    }

    public void setQuantityTotal(Long quantityTotal) {
        this.quantityTotal = quantityTotal;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriceInCent(Long priceInCent) {
        this.priceInCent = priceInCent;
    }

    public String getDescription() {
        return description;
    }

    public Long getPriceInCent() {
        return priceInCent;
    }

}