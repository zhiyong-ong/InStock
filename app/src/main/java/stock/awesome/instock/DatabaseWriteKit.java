package stock.awesome.instock;

import android.util.Log;

import com.firebase.client.Firebase;

import java.util.LinkedHashMap;
import java.util.Map;

import stock.awesome.instock.misc_classes.Kit;
import stock.awesome.instock.misc_classes.Product;
import stock.awesome.instock.misc_classes.ProductInKit;
import stock.awesome.instock.misc_classes.TestKit;


// write method overwrites existing kit. To add more products, use addProductsToKit
public class DatabaseWriteKit {

    private static final Firebase database = DatabaseLauncher.database;


    public static void write(Kit kit) throws IllegalArgumentException {
        if (kit.getKitName() == null || kit.getKitName().equals("")) {
            throw new IllegalArgumentException("Kit has invalid name");
        }
        Firebase ref = database.child("kits").child(kit.getKitName());

        ref.setValue(kit);
    }


    public static void addProductsToKit(String kitName, String prodId, int qty) throws IllegalArgumentException {
        Kit kitWithProduct = new Kit(kitName);
        ProductInKit pink = new ProductInKit(prodId, qty);
        kitWithProduct.addProduct(pink);
        addProductsToKit(kitWithProduct);
    }

    public static void addProductsToKit(String kitName, ProductInKit pink) throws IllegalArgumentException {
        Kit kitWithProduct = new Kit(kitName);
        kitWithProduct.addProduct(pink);
        addProductsToKit(kitWithProduct);
    }

    public static void addProductsToKit(Kit kit) throws IllegalArgumentException {
        if (kit.getKitName() == null || kit.getKitName().equals("")) {
            throw new IllegalArgumentException("No kit name given");
        }

        DatabaseReadKit.updateKit(kit, DatabaseReadKit.KitUseCase.UPDATE_KIT);
    }

}
