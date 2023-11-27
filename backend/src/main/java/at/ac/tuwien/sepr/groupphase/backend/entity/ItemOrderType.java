package at.ac.tuwien.sepr.groupphase.backend.entity;

public enum ItemOrderType {

    NAME("product_name"),
    DATE("expire_date"),
    QUANTITY("quantity_current");

    final String columnName;

    ItemOrderType(String columnName) {
        this.columnName = columnName;
    }
}
