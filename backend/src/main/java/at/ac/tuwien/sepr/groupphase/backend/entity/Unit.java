package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import java.util.Objects;

@Entity
public class Unit {

    @Id
    private String unit;

    @Nullable
    private Long convertFactor;

    @OneToOne
    private Unit subUnit;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Nullable
    public Long getConvertFactor() {
        return convertFactor;
    }

    public void setConvertFactor(@Nullable Long convertFactor) {
        this.convertFactor = convertFactor;
    }

    public Unit getSubUnit() {
        return subUnit;
    }

    public void setSubUnit(Unit subUnit) {
        this.subUnit = subUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Unit unit1 = (Unit) o;
        return Objects.equals(unit, unit1.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unit);
    }

    @Override
    public String toString() {
        return "Unit{"
            + "unit='" + unit + '\''
            + '}';
    }
}
