package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

//TODO: replace this class with a correct ApplicationUser Entity implementation
@Entity(name = "application_user")
public class ApplicationUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column(unique = true)
    private String email;
    @Column
    private String password;
    @Column
    private Boolean admin;
    @ManyToOne
    private SharedFlat sharedFlat;
    @OneToMany
    private List<Expense> myExpense;
    @OneToMany(mappedBy = "user")
    private List<Debit> debits;
    @Enumerated(EnumType.STRING)
    private Role role;


    public ApplicationUser() {
    }

    public ApplicationUser(Long id, String firstName, String lastName, String email, String password, Boolean admin, SharedFlat sharedFlat) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.admin = admin;
        this.sharedFlat = sharedFlat;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Long getId() {
        return id;
    }

    public void setSharedFlat(SharedFlat existingSharedFlat) {
        this.sharedFlat = existingSharedFlat;
        if (existingSharedFlat != null) {
            Set<ApplicationUser> users = existingSharedFlat.getUsers();
            users.add(this);
            existingSharedFlat.setUsers(users);
        }
    }

    public SharedFlat getSharedFlat() {
        return sharedFlat;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Expense> getMyExpense() {
        return myExpense;
    }

    public void setMyExpense(List<Expense> expense) {
        this.myExpense = expense;
    }

    public List<Debit> getDebits() {
        return debits;

    @Override
    public boolean equals (Object o){
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApplicationUser user = (ApplicationUser) o;
        return Objects.equals(id, user.id);
    }

    public void setDebits (List < Debit > debits) {
        this.debits = debits;
        debits.forEach(debit -> debit.setUser(this));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
