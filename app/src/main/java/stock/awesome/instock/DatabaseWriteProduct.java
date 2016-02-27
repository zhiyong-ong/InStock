package stock.awesome.instock;

import android.util.Log;

import com.firebase.client.Firebase;

import stock.awesome.instock.exceptions.ProductNotFoundException;

/**
 * writeProduct writes a product and its associated information to the database.
 * It can be accessed again through searching for its ID.
 * Product data is stored as strings except for quantity (stored as an integer).
 */
public class DatabaseWriteProduct {

    private static final Firebase database = DatabaseLauncher.database;


    // writes all the characteristic data of a product to database.
    // must have id, other values optional. All string fields are initialised with null values
    // and integer fields with -1.
    public static void write(Product product) throws ProductNotFoundException {
        Firebase ref = database.child("products").child(product.getId());

        if (product.getId() == null) {
            throw new ProductNotFoundException("No product ID given");
        }

        else if (product.getName().equals("set_as_null")) {
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
    public static void updateProduct(Product product) throws ProductNotFoundException {
        try {
            DatabaseReadProduct.read(product.getId(), DatabaseReadProduct.ProdUseCase.UPDATE_PRODUCT, product);
        } catch (ProductNotFoundException e) {
            throw new ProductNotFoundException(e.getMessage());
        }
    }


    // To update quantity of a product, pass in id and change in qty (pos/neg)
    public static void updateQuantity(String id, int qtyChange) throws ProductNotFoundException {
        try {
            DatabaseReadProduct.read(id, DatabaseReadProduct.ProdUseCase.UPDATE_QUANTITY_ONLY, qtyChange);
        } catch (ProductNotFoundException e) {
            throw new ProductNotFoundException(e.getMessage());
        }
    }


    // To delete, pass in id of product to delete
    public static void deleteProduct(String id) throws ProductNotFoundException {
        try {
            DatabaseReadProduct.read(id, DatabaseReadProduct.ProdUseCase.DELETE_PRODUCT);
        } catch (ProductNotFoundException e) {
            throw new ProductNotFoundException(e.getMessage());
        }

    }

}




