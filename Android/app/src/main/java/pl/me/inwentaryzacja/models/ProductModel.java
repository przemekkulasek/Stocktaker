package pl.me.inwentaryzacja.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ProductModel implements Serializable, Parcelable {

    private int id;
    private int scanId;
    private String code;
    private String description;
    private String codeType;
    private int quantity;

    public ProductModel(int id, int scanId, String code, String description, String codeType, int quantity) {
        this.id = id;
        this.scanId = scanId;
        this.code = code;
        this.description = description;
        this.codeType = codeType;
        this.quantity = quantity;
    }

    public ProductModel(Parcel in) {
        this.id = in.readInt();
        this.scanId = in.readInt();
        this.code = in.readString();
        this.description = in.readString();
        this.codeType = in.readString();
        this.quantity = in.readInt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScanId() {
        return scanId;
    }

    public void setScanId(int scanId) {
        this.scanId = scanId;
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

    public String getCodeType() { return codeType; }

    public void setCodeType(String codeType) { this.codeType = codeType; }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ProductModel{" +
                "id=" + id +
                ", scanId=" + scanId +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", codeType='" + codeType + '\'' +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(scanId);
        dest.writeString(code);
        dest.writeString(description);
        dest.writeString(codeType);
        dest.writeInt(quantity);
    }

    public static final Parcelable.Creator<ProductModel> CREATOR = new Parcelable.Creator<ProductModel>() {
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };
}
