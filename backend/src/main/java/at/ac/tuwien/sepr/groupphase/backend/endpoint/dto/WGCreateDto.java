package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class WGCreateDto {
    @NotNull(message = "Name must not be null")
    private String name;
    @NotNull(message = "Address must not be null")
    private String address;
    @NotNull(message = "Password must not be null")
    private String password;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
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
        if (!(o instanceof WGCreateDto wgCreateDto)) {
            return false;
        }
        return Objects.equals(name, wgCreateDto.name)
            && Objects.equals(password, wgCreateDto.password)
            && Objects.equals(address, wgCreateDto.address);
    }

    public int hashCode() {
        return Objects.hash(name,address, password);
    }

    public String toString() {
        return "WGCreateDto{"
            + "name='" + name + '\''
            + "address='" + address + '\''
            + ", password='" + password + '\''
            + '}';
    }
}
