package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class WGLoginDto {
    @NotNull(message = "Name must not be null")
    private String name;
    @NotNull(message = "Password must not be null")
    private String password;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WGLoginDto wgLoginDto)) {
            return false;
        }
        return Objects.equals(name, wgLoginDto.name)
            && Objects.equals(password, wgLoginDto.password);
    }

    public int hashCode() {
        return Objects.hash(name, password);
    }

    public String toString() {
        return "WGLoginDto{"
            + "name='" + name + '\''
            + ", password='" + password + '\''
            + '}';
    }
}
