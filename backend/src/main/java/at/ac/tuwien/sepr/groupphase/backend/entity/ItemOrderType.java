package at.ac.tuwien.sepr.groupphase.backend.entity;

public enum ItemOrderType {

    PRODUCT_NAME("productName"),
    EXPIRE_DATE("expireDate"),
    QUANTITY_CURRENT("quantityCurrent");


    final String columnName;

    ItemOrderType(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }
}
