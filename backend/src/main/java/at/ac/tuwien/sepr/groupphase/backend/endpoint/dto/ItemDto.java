package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDate;

public class ItemDto {

    private Long itemId;
    private String ean;
    private String generalName;
    private String productName;
    private String brand;
    private Long quantityCurrent;
    private Long quantityTotal;
    private String unit;
    private LocalDate expireDate;
    private String description;
    private Long priceInCent;

    public ItemDto(
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
        Long priceInCent
    ) {
        this.itemId = itemId;
        this.ean = ean;
        this.generalName = generalName;
        this.productName = productName;
        this.brand = brand;
        this.quantityCurrent = quantityCurrent;
        this.quantityTotal = quantityTotal;
        this.unit = unit;
        this.expireDate = expireDate;
        this.description = description;
        this.priceInCent = priceInCent;
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