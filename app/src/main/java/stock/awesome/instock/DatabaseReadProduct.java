package stock.awesome.instock;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import stock.awesome.instock.misc_classes.Kit;
import stock.awesome.instock.misc_classes.Product;
import stock.awesome.instock.misc_classes.StringCalendar;

/**
 * read(final String id, final ProdUseCase useCase, final Product updatedProd, final int qtyChange)
 * reads from database and performs operations on a product associated with that id, depending
 * on the useCase passed in.
 * Check logcat for errors.
 * WARNING: no error checking is done to determine if the appropriate useCase has been passed in.
 */
public class DatabaseReadProduct {

    private static final Firebase database = DatabaseLauncher.database;
    private static final String READ_FAILED = "Product read failed";
    public static final int THRESHOLD = 5;
    private static Product outProd = new Product();

    public enum ProdUseCase {
        DISPLAY_SEARCH, DISPLAY_PRODUCT, UPDATE_PRODUCT, UPDATE_QUANTITIES, UPDATE_QUANTITY_EXPIRY, DELETE_PRODUCT, DEBUG, DISPLAY_SEARCH_RESULT
    }


    // useCase DEBUG, DELETE_PRODUCT, BUILD_KIT
    public static void read(@NotNull final String id, @NotNull final ProdUseCase useCase) throws IllegalArgumentException {
        if (id.equals("")) {
            throw new IllegalArgumentException("Invalid product ID (empty string)");
        }
        if ( !(useCase.equals(ProdUseCase.DEBUG) || useCase.equals(ProdUseCase.DELETE_PRODUCT)
                || useCase.equals(ProdUseCase.DISPLAY_PRODUCT)
                || useCase.equals(ProdUseCase.DISPLAY_SEARCH) || useCase.equals(ProdUseCase.DISPLAY_SEARCH_RESULT)) ) {
            throw new IllegalArgumentException ("useCase must be DEBUG, DELETE_PRODUCT, DISPLAY_SEARCH, DISPLAY_PRODUCT or BUILD_KIT");
        }

        read(id, useCase, new Product(id));
    }

    // useCase UPDATE_QUANTITIES
    public static void read(@NotNull final ArrayList<Product> updatedProducts, @NotNull final ProdUseCase useCase) {
        if ( !(useCase.equals(ProdUseCase.UPDATE_QUANTITIES)) ) {
            throw new IllegalArgumentException ("useCase must be UPDATE_QUANTITY");
        }

        updateQuantities(updatedProducts);
    }

    // useCase UPDATE_PRODUCT, UPDATE_QUANTITY_EXPIRY
    public static void read(@NotNull final Product updatedProd, @NotNull final ProdUseCase useCase) throws IllegalArgumentException {
        if ( !(useCase.equals(ProdUseCase.UPDATE_PRODUCT) || useCase.equals(ProdUseCase.UPDATE_QUANTITY_EXPIRY)) ) {
            throw new IllegalArgumentException ("useCase must be UPDATE_PRODUCT or UPDATE_QUANTITY_EXPIRY");
        }

        read(updatedProd.getId(), useCase, updatedProd);
    }

    // returns a product with the name, quantity and location associated with id passed in
    // by looking up the id's characteristics in the database
    private static void read(@NotNull final String id, @NotNull final ProdUseCase useCase, @NotNull final Product updatedProd) {

        outProd = new Product();

        Firebase ref = database.child("products");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot bigSnapshot) {
                Log.d("onDataChange started ", "success");

                DataSnapshot snapshot = bigSnapshot.child(id);

                // the product does not exist in the database
                if (!snapshot.exists()) {
                    Log.e(READ_FAILED, "Product ID " + id + " not found in database");
                    if(useCase.equals(ProdUseCase.DISPLAY_SEARCH)) {
                        UpdateItemActivity.noSuchProduct();
                    }
                    else if(useCase.equals(ProdUseCase.DISPLAY_SEARCH_RESULT)) {
                        MainPage.noSuchProduct();
                    }

                } else {
                    outProd = snapshot.getValue(Product.class);

                    switch (useCase) {
                        case DISPLAY_SEARCH:
                            UpdateItemActivity.SearchItem(outProd);
                            break;

                        case DISPLAY_PRODUCT:
                            BuildKitActivity.displayItem(outProd);
                            break;

                        case DISPLAY_SEARCH_RESULT:
                            MainPage.getSearchItem(outProd);
                            break;

                        // if use case is to update quantity and expiry only,
                        // set outProd's qty to qty and write new qty, expiry to database
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

                            // Remove product from all kits that contain them
                            database.child("kits").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot allKitsSnapshot) {
                                    // For every kit
                                    for (DataSnapshot kitSnapshot : allKitsSnapshot.getChildren()) {
                                        Kit kit = kitSnapshot.getValue(Kit.class);

                                        // For every product in the kit
                                        for (String prodId: kit.getKitMap().keySet()) {
                                            if (prodId.equals(id)) {
                                                DatabaseWriteKit.removeProductsFromKit(kit.getKitName(), prodId);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                    Log.e(READ_FAILED, firebaseError.getMessage());
                                }
                            });


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


    private static void updateQuantities(final ArrayList<Product> updatedProducts) {
        Firebase ref = database.child("products");

        final ArrayList<Product> lowQtyProds = new ArrayList<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot bigSnapshot) {
                Log.d("onDataChange started ", "success");

                for (Product updatedProd : updatedProducts) {
                    DataSnapshot snapshot = bigSnapshot.child(updatedProd.getId());

                    // the product does not exist in the database
                    if (!snapshot.exists()) {
                        Log.e(READ_FAILED, "Product ID " + updatedProd.getId() + " not found in database");
                    }
                    else {
                        outProd = snapshot.getValue(Product.class);

                        int newQty = outProd.getQuantity() + updatedProd.getQuantity();
                        outProd.setQuantity(newQty);

                        DatabaseWriteProduct.write(outProd);

                        // Threshold for sending email
                        if (newQty < THRESHOLD) {
                            lowQtyProds.add(outProd);
                        }
                    }
                }

                if (lowQtyProds.size() > 0) {
                    ViewKitDetailsActivity.sendEmail(lowQtyProds);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(READ_FAILED, firebaseError.getMessage());
            }
        });
    }
}

