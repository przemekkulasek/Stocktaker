package pl.me.inwentaryzacja.webapiDA.models;

public class Location {
    private String code;
    private String description;

    public Location(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Location{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
