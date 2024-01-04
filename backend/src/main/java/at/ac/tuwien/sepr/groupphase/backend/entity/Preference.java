package at.ac.tuwien.sepr.groupphase.backend.entity;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import java.util.List;
import java.util.Objects;

@Entity(name = "preference") // name of the table
public class Preference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long firstId;
    @Column
    private Long secondId;
    @Column
    private Long thirdId;
    @Column
    private Long fourthId;
    @OneToOne
    private ApplicationUser user;

    public Long getId() {
        return id;
    }


    public Long getFirstId() {
        return firstId;
    }

    public Long getSecondId() {
        return secondId;
    }

    public Long getThirdId() {
        return thirdId;
    }

    public Long getFourthId() {
        return fourthId;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setUserId(ApplicationUser user) {
        this.user = user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstId(Long firstId) {
        this.firstId = firstId;
    }

    public void setSecondId(Long secondId) {
        this.secondId = secondId;
    }

    public void setThirdId(Long thirdId) {
        this.thirdId = thirdId;
    }

    public void setFourthId(Long fourthId) {
        this.fourthId = fourthId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Preference that = (Preference) o;
        return Objects.equals(id, that.id) && Objects.equals(firstId, that.firstId)
            && Objects.equals(secondId, that.secondId)
            && Objects.equals(thirdId, that.thirdId)
            && Objects.equals(fourthId, that.fourthId)
            && Objects.equals(user, that.user);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, firstId, secondId, thirdId, fourthId, user);
    }
}
