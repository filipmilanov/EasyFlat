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
    private List<RecipeIngredient> extendedIngredients;

    private String summary;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setExtendedIngredients(List<RecipeIngredient> extendedIngredients) {
        this.extendedIngredients = extendedIngredients;
    }

    public Integer getReadyInMinutes() {
        return readyInMinutes;
    }

    public Integer getServings() {
        return servings;
    }

    public Integer getVersion() {
        return version;
    }

    public List<RecipeIngredient> getExtendedIngredients() {
        return extendedIngredients;
    }

    public String getSummary() {
        return summary;
    }

    public String getTitle() {
        return title;
    }

    public void setReadyInMinutes(Integer readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
