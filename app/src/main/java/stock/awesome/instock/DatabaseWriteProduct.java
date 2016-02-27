package stock.awesome.instock;

import com.firebase.client.Firebase;

/**
 * writeProduct writes a product and its associated information to the database.
 * It can be accessed again through searching for its ID.
 * Product data is stored as strings except for quantity (stored as an integer).
 */
public class DatabaseWriteProduct {

    private final static Firebase database = DatabaseLauncher.database;

    public DatabaseWriteProduct() {
    }

    // writes all the characteristic data of a product to database.
    // must have id, other values optional. All string fields are initialised with null values
    // and integer fields with -1.
    public void writeProduct(Product product, DatabaseReadProduct.ProdUseCase useCase) {
        Firebase ref = database.child("products").child(product.getId());
//        Map<String, String> newProd = new HashMap<String, String>();

        // write only quantity
        switch (useCase) {
            case UPDATE_QUANTITY_ONLY:
                ref.child("quantity").setValue(product.getQuantity());
                break;

            // default behaviour. eg. if useCase UPDATE_PRODUCT
            default:
                ref.setValue(product);
                if (product.getExpiry() != null) {
                    ref.child("expiry").setValue(StringCalendar.toString(product.getExpiry()));
                }
                else {
                    ref.child("expiry").setValue(null);
                }
        }
    }
}




