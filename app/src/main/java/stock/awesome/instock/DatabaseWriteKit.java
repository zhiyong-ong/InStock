package stock.awesome.instock;

import com.firebase.client.Firebase;

import org.jetbrains.annotations.NotNull;

import stock.awesome.instock.misc_classes.Kit;
import stock.awesome.instock.misc_classes.ProductInKit;



// write method overwrites existing kit. To add more products, use addProductsToKit
public class DatabaseWriteKit {

    private static final Firebase database = DatabaseLauncher.database;


    public static void write(@NotNull Kit kit) throws IllegalArgumentException {
        if (kit.getKitName() == null || kit.getKitName().equals("")) {
            throw new IllegalArgumentException("Kit has invalid name");
        }
        Firebase ref = database.child("kits").child(kit.getKitName());

        ref.setValue(kit);
    }


    public static void addProductsToKit(@NotNull String kitName, @NotNull String prodId, int qty) throws IllegalArgumentException {
        if (kitName.equals("")) {
            throw new IllegalArgumentException("Invalid kit name given (empty string)");
        }
        Kit kitWithProduct = new Kit(kitName);
        ProductInKit pink = new ProductInKit(prodId, qty);
        kitWithProduct.addProduct(pink);
        addProductsToKit(kitWithProduct);
    }

    public static void addProductsToKit(@NotNull String kitName, @NotNull ProductInKit pink) throws IllegalArgumentException {
        if (kitName.equals("")) {
            throw new IllegalArgumentException("Invalid kit name given (empty string)");
        }
        Kit kitWithProduct = new Kit(kitName);
        kitWithProduct.addProduct(pink);
        addProductsToKit(kitWithProduct);
    }

    public static void addProductsToKit(@NotNull Kit kit) throws IllegalArgumentException {
        if (kit.getKitName().equals("")) {
            throw new IllegalArgumentException("Invalid kit name given (empty string)");
        }

        DatabaseReadKit.updateKit(kit, DatabaseReadKit.KitUseCase.UPDATE_KIT);
    }


    public static void removeProductsFromKit(@NotNull String kitName, @NotNull String id) throws IllegalArgumentException {
        if (kitName.equals("")) {
            throw new IllegalArgumentException("Invalid kit name given (empty string)");
        }
        Kit kitWithProduct = new Kit(kitName);
        kitWithProduct.addProduct(new ProductInKit(id, 0));
        removeProductsFromKit(kitWithProduct);
    }

    public static void removeProductsFromKit(@NotNull Kit kit) throws IllegalArgumentException {
        if (kit.getKitName().equals("")) {
            throw new IllegalArgumentException("Invalid kit name given (empty string)");
        }

        DatabaseReadKit.updateKit(kit, DatabaseReadKit.KitUseCase.DELETE_PRODUCTS);
    }


    public static void deleteKit(String kitName) {
        DatabaseReadKit.read(kitName, DatabaseReadKit.KitUseCase.DELETE_KIT);
    }

}
