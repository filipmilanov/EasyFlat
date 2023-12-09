package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private SplitBy splitBy;

    @Column
    private Long amountInCents;

    @ManyToOne
    private ApplicationUser paidBy;

    @OneToMany(mappedBy = "expense")
    private List<Debit> debitUsers = new ArrayList<>();

    @ManyToOne
    private SharedFlat sharedFlat;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SplitBy getSplitBy() {
        return splitBy;
    }

    public void setSplitBy(SplitBy splitBy) {
        this.splitBy = splitBy;
    }

    public Long getAmountInCents() {
        return amountInCents;
    }

    public void setAmountInCents(Long amountInCents) {
        this.amountInCents = amountInCents;
    }

    public ApplicationUser getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(ApplicationUser paidBy) {
        this.paidBy = paidBy;
        paidBy.getMyExpense().add(this);
    }

    public List<Debit> getDebitUsers() {
        return debitUsers;
    }

    public void setDebitUsers(List<Debit> debitUsers) {
        this.debitUsers = debitUsers;
        debitUsers.forEach(debit -> debit.setExpense(this));
    }

    public SharedFlat getSharedFlat() {
        return sharedFlat;
    }

    public void setSharedFlat(SharedFlat sharedFlat) {
        this.sharedFlat = sharedFlat;
        sharedFlat.getExpenses().add(this);
    }
}
