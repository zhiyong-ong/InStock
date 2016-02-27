package stock.awesome.instock.Misc_classes;

import android.util.Log;

/**
 * Created by zhiyong on 20/2/2016.
 */
public class ProductTupleKit {
    String productID = "";
    String quantity = "";

    public ProductTupleKit(String productID, String quantity) {
        this.productID = productID;
        this.quantity = quantity;
        Log.v(LOG_TAG, "-------------------TESTING on construction: " + this.productID + "\t" + this.quantity);
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getProductID() {
        return productID;
    }
    String LOG_TAG = ProductTupleKit.class.getSimpleName();
    @Override
    public String toString() {
       // String quantityAlign = String.format("%5d", "QTY: " + quantity);
        Log.v(LOG_TAG, "-------------------TESTING: " + productID + "\t" + quantity);
        return "ID: " + productID + String.format("%20s", "QTY : " + quantity);
    }
}
