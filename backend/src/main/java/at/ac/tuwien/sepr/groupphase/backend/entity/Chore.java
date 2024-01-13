package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.time.LocalDate;
import java.util.List;
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

    @ManyToOne
    @JoinColumn(name = "shared_flat_id")
    private SharedFlat sharedFlat;

    @ManyToOne(fetch = FetchType.EAGER)
    private ApplicationUser user;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Preference> firstPrefList;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Preference> secondPrefList;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Preference> thirdPrefList;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Preference> fourthPrefList;



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

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getPoints() {
        return points;
    }

    public SharedFlat getSharedFlat() {
        return sharedFlat;
    }

    public List<Preference> getFirstPrefList() {
        return firstPrefList;
    }

    public List<Preference> getSecondPrefList() {
        return secondPrefList;
    }

    public List<Preference> getThirdPrefList() {
        return thirdPrefList;
    }

    public List<Preference> getFourthPrefList() {
        return fourthPrefList;
    }

    public void setSharedFlat(SharedFlat sharedFlat) {
        this.sharedFlat = sharedFlat;
    }

    public void setFirstPrefList(List<Preference> firstPrefList) {
        this.firstPrefList = firstPrefList;
    }

    public void setSecondPrefList(List<Preference> secondPrefList) {
        this.secondPrefList = secondPrefList;
    }

    public void setThirdPrefList(List<Preference> thirdPrefList) {
        this.thirdPrefList = thirdPrefList;
    }

    public void setFourthPrefList(List<Preference> fourthPrefList) {
        this.fourthPrefList = fourthPrefList;
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
