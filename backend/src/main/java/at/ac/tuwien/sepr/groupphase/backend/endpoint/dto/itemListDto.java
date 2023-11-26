package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDate;

public class ItemListDto {

    private Long itemId;
    private String productName;
    private String brand;
    private Long quantityCurrent;
    private Long quantityTotal;
    private LocalDate expireDate;


    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getBrand() {
        return brand;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setQuantityCurrent(Long quantityCurrent) {
        this.quantityCurrent = quantityCurrent;
    }

    public void setQuantityTotal(Long quantityTotal) {
        this.quantityTotal = quantityTotal;
    }

    public Long getItemId() {
        return itemId;
    }

    public Long getQuantityCurrent() {
        return quantityCurrent;
    }

    public Long getQuantityTotal() {
        return quantityTotal;
    }

    public String getProductName() {
        return productName;
    }
}
