package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
public class ShoppingList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shopListId;

    @Column
    private String name;

    public Long getShopListId() {
        return shopListId;
    }

    public String getName() {
        return name;
    }

    public void setShopListId(Long shopListId) {
        this.shopListId = shopListId;
    }

    public void setName(String name) {
        this.name = name;
    }
}
