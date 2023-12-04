package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long itemId;

    @Column
    private String ean;

    @Column

    private String generalName;

    @Column
    @NotEmpty(message = "The product name cannot be empty")
    private String productName;

    @Column
    @NotEmpty(message = "The brand cannot be empty")
    private String brand;

    @Column
    @Min(value = 0, message = "The actual quantity must be positive")
    private Long quantityCurrent;

    @Column
    @Min(value = 0, message = "The total quantity must be positive")
    private Long quantityTotal;

    @Column
    @NotEmpty(message = "The unit cannot be empty")
    private String unit;

    @Column
    @FutureOrPresent(message = "You cannot store products which are over the expire date")
    private LocalDate expireDate;

    @Column
    @NotEmpty(message = "The description is necessary")
    private String description;

    @Column
    private Long priceInCent;

    @Column
    private String boughtAt;

    @ManyToOne
    @NotNull(message = "A Item need to be linked to a storage")
    private DigitalStorage digitalStorage;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Ingredient> ingredientList;

    @OneToMany
    private List<ItemStats> itemStats;

    @AssertTrue(message = "The current quantity cannot be larger then the total")
    private boolean quantityCurrentLessThenTotal() {
        return this.quantityCurrent < this.quantityTotal;
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
