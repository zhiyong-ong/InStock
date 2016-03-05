package stock.awesome.instock;

import com.firebase.client.Firebase;

import stock.awesome.instock.misc_classes.Product;
import stock.awesome.instock.misc_classes.StringCalendar;


/**
 * writeProduct writes a product and its associated information to the database.
 * It can be accessed again through searching for its ID.
 * Product data is stored on the database as strings except for quantity (stored as long).
 */
public class DatabaseWriteProduct {

//    private static final Firebase database = DatabaseLauncher.database;
     static Firebase database = new Firebase("https://scorching-inferno-2190.firebaseio.com/");

    // writes all the characteristic data of a product to database.
    // must have id, other values optional. All string fields are initialised with null values
    // and integer fields with -1.
    public static void write(Product product) throws IllegalArgumentException {

        if (product.getId() == null || product.getId().equals("")) {
            throw new IllegalArgumentException("No product ID given");
        }

        Firebase ref = database.child("products").child(product.getId());

        if (product.getName().equals("set_as_null")) {
            ref.removeValue();
        }

        else {
            ref.setValue(product);

            // expiry must be converted to string before being written
            if (product.getExpiry() != null) {
                ref.child("expiry").setValue(StringCalendar.toString(product.getExpiry()));
            } else {
                ref.child("expiry").setValue(null);
            }
        }
    }


    // rewrite all product information
    // IMPT: existing qty will not be increased/decreased but overwritten with product's quantity
    public static void updateProduct(Product product) throws IllegalArgumentException {
        if (product.getId() == null || product.getId().equals("")) {
            throw new IllegalArgumentException("No product ID given");
        }
        DatabaseReadProduct.read(product.getId(), DatabaseReadProduct.ProdUseCase.UPDATE_PRODUCT, product);
    }


    // To update quantity of a product, pass in id and change in qty (pos/neg)
    public static void updateQuantityExpiry(Product product) throws IllegalArgumentException {
        if (product.getId() == null || product.getId().equals("")) {
            throw new IllegalArgumentException("No product ID given");
        }
        DatabaseReadProduct.read(product.getId(), DatabaseReadProduct.ProdUseCase.UPDATE_QUANTITY_EXPIRY, product);
    }


    // To delete, pass in id of product to delete
    public static void deleteProduct(String id) throws IllegalArgumentException {
        if (id == null || id.equals("")) {
            throw new IllegalArgumentException("No product ID given");
        }
        DatabaseReadProduct.read(id, DatabaseReadProduct.ProdUseCase.DELETE_PRODUCT);
    }

}




