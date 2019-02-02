package pl.me.inwentaryzacja.webapiDA.models;

public class OperationLog {
    private int type;
    private String description;

    public OperationLog(int type, String description) {
        this.type = type;
        this.description = description;
    }

    private String getTypeDescription()
    {
        switch (type)
        {
            case 0:
                return "Wystąpił błąd";
            case 1:
                return "Ostrzeżenie";
            case 2:
                return "Sukces";
            case 3:
                return "Informacja";
            default:
                return "Informacja";
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTypeDescription());
        sb.append("! ");
        sb.append(description);
        sb.append("\n");

        return sb.toString();
    }
}
