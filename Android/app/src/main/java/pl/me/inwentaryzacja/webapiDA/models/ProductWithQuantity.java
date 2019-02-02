package pl.me.inwentaryzacja.webapiDA.models;

public class ProductWithQuantity extends Product {

    private int quantity;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
