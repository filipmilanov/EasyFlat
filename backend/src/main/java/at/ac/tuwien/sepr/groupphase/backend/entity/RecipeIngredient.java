package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class RecipeIngredient {
    @Id
    private Long id;

    private String name;

    private String unit;

    private double amount;

    @ManyToOne
    private RecipeSuggestion recipeSuggestion;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
