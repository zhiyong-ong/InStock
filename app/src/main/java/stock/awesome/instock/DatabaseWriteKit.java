package stock.awesome.instock;

import android.util.Log;

import com.firebase.client.Firebase;

import stock.awesome.instock.misc_classes.Kit;
import stock.awesome.instock.misc_classes.TestKit;


// write method overwrites existing kit. To add more products, use addProductsToKit
public class DatabaseWriteKit {

    private static final Firebase database = DatabaseLauncher.database;

    public static void write(TestKit kit) throws IllegalArgumentException {
//        if (kit.getKitName() == null || kit.getKitName().equals("")) {
//            Log.e("Kit write failed", "kit has invalid name");
//        }
        Firebase ref = database.child("kits").child("boo");
//        Log.e("kit name", kit.getKitName());
        ref.setValue("ggg");
    }


//    public static void addProductsToKit(String kitName, Product product, int qty) throws KitNotFoundException {
//        Kit kitWithProduct = new Kit(kitName);
//        kitWithProduct.addProduct(product, qty);
//
//        addProductsToKit(kitWithProduct);
//    }
//
//
//    public static void addProductsToKit(Kit kit) throws KitNotFoundException {
//        if (kit.getKitName() == null || kit.getKitName().equals("")) {
//            Log.e("Kit write failed", "kit has no name");
//            throw new KitNotFoundException("No kit name given");
//        }
//
//        Firebase ref = database.child("kits").child(kit.getKitName());
//
//        LinkedHashMap<Product, Integer> kitHashMap = kit.getHashMap();
//
//        for (Map.Entry<Product, Integer> entry : kitHashMap.entrySet())  {
//
//            Map<String, Object> newKit = new LinkedHashMap<String, Object>();
//
//            String id = entry.getKey().getId();
//            int qty = entry.getValue();
//
//            newKit.put("/id", id);
//            newKit.put("/quantity", qty);
//
//            ref.push().updateChildren(newKit);
//        }
//    }

}
