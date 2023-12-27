package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class WgDetailDto {

    private Long id;

    @NotNull(message = "Name must not be null")
    private String name;

    @NotNull(message = "Password must not be null")
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String firstName) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WgDetailDto that = (WgDetailDto) o;
        return Objects.equals(name, that.getName()) && Objects.equals(password, that.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, password);
    }

    @Override
    public String toString() {
        return "WgDetailDto{"
            + "name='" + name + '\''
            + ", password='" + password + '\''
            + '}';
    }

}

