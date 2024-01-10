package at.ac.tuwien.sepr.groupphase.backend.entity;

public enum RepeatingExpenseTyp {
    FIRST_OF_MONTH(-1),
    FIRST_OF_QUARTER(-2),
    FIST_OF_YEAR(-3);


    public final int value;

    RepeatingExpenseTyp(int value) {
        this.value = value;
    }
}
