package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@DiscriminatorValue("Shopping")
public class ShoppingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column
    private String ean;

    @Column

    private String generalName;

    @Column
    private String productName;

    @Column
    private String brand;

    @Column
    private Long quantityCurrent;

    @Column
    private Long quantityTotal;

    @Column
    @NotEmpty(message = "The unit cannot be empty")
    private String unit;

    @Column
    private LocalDate expireDate;

    @Column
    private String description;

    @Column
    private Long priceInCent;

    @Column
    private String boughtAt;

    @ManyToOne
    private DigitalStorage digitalStorage;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Ingredient> ingredientList;

    @OneToMany
    private List<ItemStats> itemStats;

    @Column
    @ManyToMany(fetch = FetchType.EAGER)
    private List<ItemLabel> labels;

    @AssertTrue(message = "The current quantity cannot be larger then the total")
    private boolean quantityCurrentLessThenTotal() {
        return this.quantityCurrent <= this.quantityTotal;
    }

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

    public String getBoughtAt() {
        return boughtAt;
    }

    public void setBoughtAt(String boughtAt) {
        this.boughtAt = boughtAt;
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

    public Long getMinimumQuantity() {
        return null;
    }

    public void setMinimumQuantity(Long minimumQuantity) {

    }

    public boolean alwaysInStock() {
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }

    public void setLabels(List<ItemLabel> labels) {
        this.labels = labels;
    }

    public List<ItemLabel> getLabels() {
        return labels;
    }
}
