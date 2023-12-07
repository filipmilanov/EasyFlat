package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class RecipeSuggestion {

    @Id
    private Long id;

    @NotEmpty
    @Column
    private String title;
    @Column
    private Integer servings;
    @Column
    private Integer readyInMinutes;

    @Version Integer version;
    @OneToMany
    private List<RecipeIngredient> ingredients;

    private String summary;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
