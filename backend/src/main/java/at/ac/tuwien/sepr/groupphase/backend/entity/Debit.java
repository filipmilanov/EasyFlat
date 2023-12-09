package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

import java.util.Objects;

@Entity
public class Debit {

    @EmbeddedId
    private DebitKey id;

    private Long percent;

    public DebitKey getId() {
        return id;
    }

    public void setId(DebitKey id) {
        this.id = id;
    }

    public Long getPercent() {
        return percent;
    }

    public void setPercent(Long percent) {
        this.percent = percent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Debit debit = (Debit) o;
        return Objects.equals(id, debit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
