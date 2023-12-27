package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.util.Objects;

@Entity(name = "chore") // name of the table
public class Chore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private LocalDate endDate;

    @Column
    private int points;

    public Chore() {
    }

    public String getName() {
        return name;
    }

    public Chore setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Chore setDescription(String description) {
        this.description = description;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Chore setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public int getPoints() {
        return points;
    }

    public Chore setPoints(int points) {
        this.points = points;
        return this;
    }

    public Long getId() {
        return id;
    }

    public Chore setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Chore chore = (Chore) o;
        return Objects.equals(id, chore.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
