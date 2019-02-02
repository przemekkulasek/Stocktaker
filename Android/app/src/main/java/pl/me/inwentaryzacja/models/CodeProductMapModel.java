package pl.me.inwentaryzacja.models;

public class CodeProductMapModel {

    private String code;
    private String description;

    public CodeProductMapModel(String code, String description) {
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
        return "CodeProductMapModel{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
