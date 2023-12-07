package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;

import java.util.List;

public class ShoppingItem extends Item {

    @Column
    @ManyToMany
    private List<ShoppingLabel> labels;
}
