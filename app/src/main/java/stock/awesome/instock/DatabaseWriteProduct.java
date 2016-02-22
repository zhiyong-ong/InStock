package stock.awesome.instock;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

/**
 * writeProduct writes a product and its associated information to the database.
 * It can be accessed again through searching for its ID.
 */
public class DatabaseWriteProduct {

    private Firebase database = null;

    public DatabaseWriteProduct(Firebase database) {
        this.database = database;
    }

    // writes all the characteristic data of a product to database.
    // must have id, other values optional. All string fields are initialised with null values
    // and integer fields with -1 except expiry. If expiry date is null,
    // that field is not created
    public void writeProduct(Product product) {
        Firebase ref = database.child("products").child(product.getId());
        Map<String, String> newProd = new HashMap<String, String>();

        newProd.put("name", product.getName());
        newProd.put("desc", product.getDesc());
        newProd.put("location", product.getLocation());
        newProd.put("quantity", Integer.toString(product.getQuantity()));
        if (product.getExpiry() != null)
            newProd.put("expiry", CalendarAsStr.format(product.getExpiry()));

        ref.setValue(newProd);
    }
}


