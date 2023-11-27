package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("AlwaysInStock")
public class AlwaysInStockItem extends Item {

    @Column
    @Min(value = 0, message = "The minimum quantity must be positive")
    @NotNull(message = "The minimum quantity cannot be empty")
    private Long minimumQuantity;

    public Long getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(Long minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public boolean alwaysInStock() {
        return true;
    }
}
