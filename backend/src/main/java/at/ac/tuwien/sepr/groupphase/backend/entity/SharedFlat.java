package at.ac.tuwien.sepr.groupphase.backend.entity;

import org.springframework.boot.autoconfigure.web.WebProperties;

public class SharedFlat {
    private String name;
    private String password;

    public SharedFlat(String name, String password){
        this.name = name;
        this.password = password;
    }
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
}
