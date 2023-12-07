package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

import java.util.List;

@Entity
public class ShoppingItem extends Item {

    @Column
    @ManyToMany
    private List<ItemLabel> labels;
}
