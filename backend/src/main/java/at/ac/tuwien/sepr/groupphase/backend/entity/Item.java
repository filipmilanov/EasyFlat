package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column
    @Pattern(regexp = "[0-9](13)", message = "EAN number has exactly 13 numbers")
    private String ean;

    @Column
    @NotEmpty
    private String generalName;

    @Column
    @NotEmpty
    private String productName;

    @Column
    @NotEmpty
    private String brand;

    @Column
    @Min(value = 0, message = "The quantity must be positive")
    private Long quantityCurrent;

    @Column
    @Min(value = 0, message = "The quantity must be positive")
    private Long quantityTotal;

    @Column
    @NotEmpty
    private String unit;

    @Column
    private LocalDate expireDate;

    @Column
    private String description;

    @Column
    private Long priceInCent;

    @ManyToOne
    private DigitalStorage digitalStorage;

    @ManyToMany
    private List<Ingredient> ingredientList;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long id) {
        this.itemId = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPriceInCent() {
        return priceInCent;
    }

    public void setPriceInCent(Long priceInCent) {
        this.priceInCent = priceInCent;
    }

    public DigitalStorage getStorage() {
        return digitalStorage;
    }

    public void setStorage(DigitalStorage digitalStorage) {
        this.digitalStorage = digitalStorage;
    }

    public List<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Item item = (Item) o;
        return Objects.equals(itemId, item.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }


}
