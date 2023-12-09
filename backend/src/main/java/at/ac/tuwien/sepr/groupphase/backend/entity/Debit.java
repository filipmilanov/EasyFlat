package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

import java.util.Objects;

@Entity
public class Debit {

    @EmbeddedId
    private DebitKey id;

    @ManyToOne
    @MapsId("expenseId")
    private Expense expense;

    @ManyToOne
    @MapsId("userId")
    private ApplicationUser user;

    private Long percent;

    public DebitKey getId() {
        return id;
    }

    public void setId(DebitKey id) {
        this.id = id;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
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
