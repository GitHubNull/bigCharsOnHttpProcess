package top.oxff.model;

public enum HTTPRequestBodyType {
    JSON("json"),
    XML("xml"),
    FORM("form"),
    URL_ENCODE_FORM("url-encode-form"),
    BINARY("binary"),
    UNKNOWN("unknown");

    private String name;

    HTTPRequestBodyType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
