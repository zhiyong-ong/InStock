package stock.awesome.instock;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import stock.awesome.instock.misc_classes.Product;
import stock.awesome.instock.misc_classes.StringCalendar;
import stock.awesome.instock.fragments.UpdateItemFragment;

/**
 * read(final String id, final ProdUseCase useCase, final Product updatedProd, final int qtyChange)
 * reads from database and performs operations on a product associated with that id, depending
 * on the useCase passed in.
 * Check logcat for errors.
 * WARNING: no error checking is done to determine if the appropriate useCase has been passed in.
 */
public class DatabaseReadProduct {

    private static final Firebase database = DatabaseLauncher.database;
    private static Product outProd = new Product();
    private static final String READ_FAILED = "Product read failed";
    private static final String WRITE_FAILED = "Product write failed";

    public enum ProdUseCase {
        BUILD_KIT, DISPLAY, UPDATE_PRODUCT, UPDATE_QUANTITY_EXPIRY, DELETE_PRODUCT, DEBUG
    }


    // useCase DEBUG, DELETE_PRODUCT, UPDATE_PRODUCT, BUILD_KIT
    public static void read(final String id,  final ProdUseCase useCase) {
        read(id, useCase, null);
    }

    // returns a product with the name, quantity and location associated with id passed in
    // by looking up the id's characteristics in the database
    public static void read(final String id, final ProdUseCase useCase, final Product updatedProd) {

        outProd = new Product();

        if (id == null || id.equals("")) {
            Log.e(READ_FAILED, "No product ID given");
        }

        Firebase ref = database.child("products");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot bigSnapshot) {
                Log.d("onDataChange started ", "success");

                DataSnapshot snapshot = bigSnapshot.child(id);

                // the product does not exist in the database
                if (!snapshot.exists()) {
                    Log.e(READ_FAILED, "Product name: " + id + " not found in database");
                }
                else {
                    outProd = snapshot.getValue(Product.class);

                    switch (useCase) {
                        case DISPLAY:
                            UpdateItemFragment.SearchItem(outProd);
                            break;

                        case BUILD_KIT:
//                            BuildKitActivity.displayProduct(result);
                            break;

                        // if use case is to update quantity only, set outProd's qty to qty
                        // and write new qty to database
                        case UPDATE_QUANTITY_EXPIRY:
                            int qty = (int) (long) snapshot.child("quantity").getValue();

                            outProd.setQuantity(qty + updatedProd.getQuantity());
                            outProd.setExpiry(updatedProd.getExpiry());

                            DatabaseWriteProduct.write(outProd);
                            break;

                        // if use case is to update product, no reading required.
                        // only check needed is that item exists in database, which is handled above
                        // after check, product is written to database
                        case UPDATE_PRODUCT:
                            DatabaseWriteProduct.write(updatedProd);
                            break;

                        case DELETE_PRODUCT:
                            Product emptyProd = new Product();
                            emptyProd.setId(id);
                            emptyProd.setName("set_as_null");

                            DatabaseWriteProduct.write(emptyProd);
                            break;

                        // log product's characteristics
                        case DEBUG:
                            Log.w("result info", outProd.getId() + " " + outProd.getName() + " " +
                                    outProd.getQuantity() + " " + StringCalendar.toString(outProd.getExpiry()));
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(READ_FAILED, firebaseError.getMessage());
            }
        });
    }
}

