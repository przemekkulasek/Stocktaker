package pl.me.inwentaryzacja.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ScannedCodeModel implements Parcelable {

    private String code;
    private String type;
    private String description;
    private int quantity;

    public ScannedCodeModel(String code, String type, String description, int quantity) {
        this.code = code;
        this.type = type;
        this.description = description;
        this.quantity = quantity;
    }

    public ScannedCodeModel(Parcel in) {
        this.code = in.readString();
        this.type = in.readString();
        this.description = in.readString();
        this.quantity = in.readInt();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ScannedCodeModel{" +
                "code='" + code + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(type);
        dest.writeString(description);
        dest.writeInt(quantity);
    }

    public static final Parcelable.Creator<ScannedCodeModel> CREATOR = new Parcelable.Creator<ScannedCodeModel>() {
        public ScannedCodeModel createFromParcel(Parcel in) {
            return new ScannedCodeModel(in);
        }

        public ScannedCodeModel[] newArray(int size) {
            return new ScannedCodeModel[size];
        }
    };
}
