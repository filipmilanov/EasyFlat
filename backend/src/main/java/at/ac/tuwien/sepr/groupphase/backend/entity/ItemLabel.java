package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ItemLabel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shopLabelId;

    @Column
    private String labelValue;

    @Column
    private String labelColour;

}
