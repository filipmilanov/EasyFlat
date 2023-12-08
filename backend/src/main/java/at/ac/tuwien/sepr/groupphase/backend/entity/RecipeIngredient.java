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

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RecipeSuggestion getRecipeSuggestion() {
        return recipeSuggestion;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public void setRecipeSuggestion(RecipeSuggestion recipeSuggestion) {
        this.recipeSuggestion = recipeSuggestion;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
