package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class WgDetailDto {

    private Long id;

    @NotEmpty(message = "Name must not be empty")
    @Size(max = 100, message = "Name cannot be larger than 100 characters")
    private String name;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "The password must be at least 8 characters")
    @Size(max = 100, message = "The password cannot be larger than 100 characters")
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
        this.name = firstName;
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

