package at.ac.tuwien.sepr.groupphase.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
public class ShoppingList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<ShoppingItem> items;

    @ManyToOne
    private SharedFlat sharedFlat;


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ShoppingList setId(Long shopListId) {
        this.id = shopListId;
        return this;
    }

    public ShoppingList setName(String name) {
        this.name = name;
        return this;
    }

    public List<ShoppingItem> getItems() {
        return items;
    }

    public void setItems(List<ShoppingItem> items) {
        this.items = items;
    }

    @JsonBackReference
    public SharedFlat getSharedFlat() {
        return sharedFlat;
    }

    public void setSharedFlat(SharedFlat sharedFlat) {
        this.sharedFlat = sharedFlat;
    }

}
