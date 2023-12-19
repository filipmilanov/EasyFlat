package at.ac.tuwien.sepr.groupphase.backend.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class ItemStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemStatId;

    @Column
    private LocalDate dateOfPurchase;

    @Column
    private Long amountSpendOn;

    public Long getItemStatId() {
        return itemStatId;
    }

    public void setItemStatId(Long itemStatId) {
        this.itemStatId = itemStatId;
    }

    public Long getAmountSpendOn() {
        return amountSpendOn;
    }

    public void setAmountSpendOn(Long oldPrice) {
        this.amountSpendOn = oldPrice;
    }

    public void setDateOfPurchase(LocalDate dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
    }
}
