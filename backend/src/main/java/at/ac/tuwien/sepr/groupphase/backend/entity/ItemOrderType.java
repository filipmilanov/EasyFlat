package at.ac.tuwien.sepr.groupphase.backend.entity;

public enum ItemOrderType {

    NAME("productName"),
    DATE("expireDate"),
    QUANTITY("quantityCurrent");

    final String columnName;

    ItemOrderType(String columnName) {
        this.columnName = columnName;
    }
}
