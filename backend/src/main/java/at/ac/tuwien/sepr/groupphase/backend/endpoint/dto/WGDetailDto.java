package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class WGDetailDto {
    @NotNull(message = "Name must not be null")
    private String name;

    @NotNull(message = "Password must not be null")
    private String password;

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
        WGDetailDto that = (WGDetailDto) o;
        return Objects.equals(name, that.getName()) && Objects.equals(password, that.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, password);
    }

    @Override
    public String toString() {
        return "WGDetailDto{" +
            "name='" + name + '\'' +
            ", password='" + password + '\'' +
            '}';
    }

}
