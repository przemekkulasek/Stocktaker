package pl.me.inwentaryzacja.models;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import pl.me.inwentaryzacja.models.ProductModel;
import pl.me.inwentaryzacja.models.ScanModel;
import pl.me.inwentaryzacja.webapiDA.models.Product;

public class ScanGroupModel {
    public ScanModel savedScanModel;
    public final ArrayList<ProductModel> children = new ArrayList<ProductModel>();

    public ScanGroupModel(ScanModel savedScanModel) {
        this.savedScanModel = savedScanModel;
    }

    public boolean isRecognized() {
        for(ProductModel productModel: children)
        {
            if(productModel.getDescription() == null)
            {
                return false;
            }
        }
        return true;
    }

    public String[] getNotRecognizedProductCodes()
    {
        List<String> productIds = new ArrayList<>();

        for (ProductModel product : this.children) {
            productIds.add(product.getCode());
        }

        return productIds.toArray(new String[productIds.size()]);
    }
}
