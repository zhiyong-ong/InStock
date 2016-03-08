package stock.awesome.instock.misc_classes;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;


// collection of products and associated quantities to be picked.
// Order input == order output.
// addProduct throws IllegalArgumentException if id is null
public class Kit {

    // key: string id, value: prodInKit with that id
    private LinkedHashMap<String, ProductInKit> kitMap;
    private String kitName = null;

    public Kit() {
    }

    // throws IllegalArgumentException if name is null or empty
    public Kit(@NotNull String kitName) throws IllegalArgumentException {
        if (kitName.equals("")) {
            throw new IllegalArgumentException("Invalid kit name given");
        }
        kitMap = new LinkedHashMap<String, ProductInKit>();
        this.kitName = kitName;
    }


    // throws IllegalArgumentException if name is null or empty
    public void setKitName(String name) throws IllegalArgumentException {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("Invalid kit name");
        }
        kitName = name;
    }

    public String getKitName() {
        return kitName;
    }


    public LinkedHashMap<String, ProductInKit> getKitMap() {
        return kitMap;
    }

    public void setKitMap(LinkedHashMap<String, ProductInKit> kit) throws IllegalArgumentException {
        if (kit == null) {
            throw new IllegalArgumentException("Null kit given");
        }
        this.kitMap = kit;
    }


    // returns product (has id and qty) associated with id
    public ProductInKit getProduct(String id) {
        return kitMap.get(id);
    }

    // removes id and associated product
    public void removeProduct(String id) {
        kitMap.remove(id);
    }


    // adds as key value pair of id:prodInKit
    public void addProduct(Product product, int qty) {
        ProductInKit pink = new ProductInKit(product.getId(), qty);
        addProduct(pink);
    }

    // adds as key value pair of id:prodInKit
    // throws IllegalArgumentException if product has no id or is empty
    public void addProduct(String id, int qty) throws IllegalArgumentException {
        ProductInKit pink = new ProductInKit(id, qty);
        addProduct(pink);
    }

    // adds as key value pair of id:prodInKit
    public void addProduct(ProductInKit pink) {
        kitMap.put(pink.getId(), pink);
    }

}
