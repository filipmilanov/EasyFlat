package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ingrId;

    @Column
    private String title;

    @ManyToMany(mappedBy = "ingredientList")
    private List<DigitalStorageItem> digitalStorageItemList = new ArrayList<>();

    public Long getIngrId() {
        return ingrId;
    }

    public void setIngrId(Long ingrId) {
        this.ingrId = ingrId;
    }

    public String getTitle() {
        return title;
    }

    public Ingredient setTitle(String title) {
        this.title = title;
        return this;
    }

    public List<DigitalStorageItem> getItemList() {
        return digitalStorageItemList;
    }

    public void setItemList(List<DigitalStorageItem> digitalStorageItemList) {
        this.digitalStorageItemList = digitalStorageItemList;
        if (digitalStorageItemList != null) {
            digitalStorageItemList.forEach(item -> item.getIngredientList().add(this));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Ingredient that = (Ingredient) o;
        return Objects.equals(ingrId, that.ingrId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingrId);
    }
}
