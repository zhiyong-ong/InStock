package stock.awesome.instock;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.ValueEventListener;

import stock.awesome.instock.exceptions.ProductNotFoundException;

import stock.awesome.instock.Misc_classes.Product;
import stock.awesome.instock.Misc_classes.StringCalendar;
import stock.awesome.instock.fragments.UpdateItemFragment;

/**
 * read(final String id, final ProdUseCase useCase, final Product updatedProd, final int qtyChange)
 * reads from database and performs operations on a product associated with that id, depending
 * on the useCase passed in.
 * WARNING: no error checking is done to determine if the appropriate useCase has been passed in.
 */
public class DatabaseReadProduct {

    private static final Firebase database = DatabaseLauncher.database;
    private static Product outProd = new Product();
    private static ProductNotFoundException e = null;

    public enum ProdUseCase {
        BUILD_KIT, DISPLAY, UPDATE_PRODUCT, UPDATE_QUANTITY_EXPIRY, DELETE_PRODUCT, DEBUG
    }


    // useCase DEBUG, DELETE_PRODUCT
    public static void read(final String id,  final ProdUseCase useCase)
            throws ProductNotFoundException, FirebaseException {
        read(id, useCase, null, 0);
    }

    // useCase UPDATE_PRODUCT, BUILD_KIT
    public static void read(final String id,  final ProdUseCase useCase, final Product updatedProd)
            throws ProductNotFoundException, FirebaseException {
        read(id, useCase, updatedProd, 0);
    }


    // returns a product with the name, quantity and location associated with id passed in
    // by looking up the id's characteristics in the database
    public static void read(final String id, final ProdUseCase useCase,
                                      final Product updatedProd, final int qtyChange)
            throws ProductNotFoundException, FirebaseException {

        outProd = new Product();
        e = null;

        if (id == null) {
            throw new ProductNotFoundException("No product ID given");
        }

        Firebase ref = database.child("products");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot bigSnapshot) {
                Log.d("onDataChange started ", "success");

                DataSnapshot snapshot = bigSnapshot.child(id);

                // the product does not exist in the database
                if (!snapshot.exists()) {
                    e = new ProductNotFoundException("Product name: " + id + " not found in database");
                }
                else {
                    Log.e("PRODUCT", "-------------------- id input : " + id);
                    outProd = snapshot.getValue(Product.class);
                    Log.e("PRODUCT", "-------------------- ID IS: " + outProd.getId());
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
                            outProd.setQuantity(qty + qtyChange);

                            outProd.setExpiry(updatedProd.getExpiry());

                            try {
                                DatabaseWriteProduct.write(outProd, e);
                            }
                            catch (ProductNotFoundException exc) {
                                e = exc;
                            }
                            break;

                        // if use case is to update product, no reading required.
                        // only check needed is that item exists in database, which is handled above
                        // after check, product is written to database
                        case UPDATE_PRODUCT:
                            try {
                                DatabaseWriteProduct.write(updatedProd, e);
                            }
                            catch (ProductNotFoundException exc) {
                                e = exc;
                            }
                            break;

                        case DELETE_PRODUCT:
                            Product emptyProd = new Product();
                            emptyProd.setId(id);
                            emptyProd.setName("set_as_null");

                            try {
                                DatabaseWriteProduct.write(emptyProd, e);
                            }
                            catch (ProductNotFoundException exc) {
                                e = exc;
                            }
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
                throw new FirebaseException(firebaseError.getMessage());
            }
        });

    }
}

