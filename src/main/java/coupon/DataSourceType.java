package coupon;

public enum DataSourceType {
    READER("reader"),
    WRITER("writer");

    private final String type;

    DataSourceType(String type) {
        this.type = type;
    }
}
