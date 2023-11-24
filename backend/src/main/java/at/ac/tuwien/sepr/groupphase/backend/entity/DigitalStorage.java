package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
public class DigitalStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storId;

    @Column
    private String title;

    @OneToMany(mappedBy = "digitalStorage")
    private List<Item> itemList;

    public Long getStorId() {
        return storId;
    }

    public void setStorId(Long id) {
        this.storId = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DigitalStorage digitalStorage = (DigitalStorage) o;
        return Objects.equals(storId, digitalStorage.storId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storId);
    }
}
