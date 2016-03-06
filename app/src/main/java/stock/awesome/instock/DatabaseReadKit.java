package stock.awesome.instock;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

import stock.awesome.instock.misc_classes.Kit;
import stock.awesome.instock.misc_classes.Product;
import stock.awesome.instock.misc_classes.ProductInKit;
import stock.awesome.instock.misc_classes.StringCalendar;


public class DatabaseReadKit {
    private static final Firebase database = DatabaseLauncher.database;
    private static Kit outKit = new Kit();
    private static final String READ_FAILED = "Kit read failed";

    public enum KitUseCase {
        UPDATE_KIT, VIEW_PRODUCT_DETAILS, DEBUG, DELETE_KIT
    }

    // useCase VIEW_PRODUCT_DETAILS, DEBUG, DELETE_KIT
    public static void read(@NotNull String kitName, @NotNull KitUseCase useCase) throws IllegalArgumentException {
        if ( !(useCase.equals(KitUseCase.VIEW_PRODUCT_DETAILS) || useCase.equals(KitUseCase.DEBUG) || useCase.equals(KitUseCase.DELETE_KIT)) ) {
            throw new IllegalArgumentException("useCase must be VIEW_PRODUCT_DETAILS, DELETE_KIT or DEBUG");
        }

        Kit emptyKit = new Kit(kitName);

        read(kitName, useCase, emptyKit);
    }

    // useCase UPDATE_KIT
    public static void updateKit(@NotNull Kit kit, @NotNull KitUseCase useCase) throws IllegalArgumentException {
        if (!(useCase.equals(KitUseCase.UPDATE_KIT))) {
            throw new IllegalArgumentException("useCase must be UPDATE_KIT");
        }

        read(kit.getKitName(), useCase, kit);
    }

    // useCase VIEW_PRODUCT_DETAILS, DEBUG
    // returns a kit with the product associated with id passed in
    // by looking up the id's characteristics in the database
    private static void read(@NotNull final String kitName, @NotNull final KitUseCase useCase, @NotNull final Kit updatedKit) {

        outKit = new Kit();

        Firebase ref = database.child("kits").child(kitName);

        Log.d("Adding listener ", "SingleValueEvent");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("onDataChange started", "success");

                // kitName not in the database
                if (!snapshot.exists()) {
                    Log.e(READ_FAILED, "Kit name " + kitName + " not found in database");
                }

                // look at kits sub-database
                else {
                    switch (useCase) {
                        // check was needed to see if kit existed
                        case UPDATE_KIT:
                            // write additional products
                            writeAddProducts(updatedKit);
                            break;

                        // check was needed to see if kit existed
                        case DELETE_KIT:
                            DatabaseReadKit.delete(kitName);
                            break;

                        case VIEW_PRODUCT_DETAILS:
                            outKit = snapshot.getValue(Kit.class);
                            break;

                        case DEBUG:
                            outKit = snapshot.getValue(Kit.class);
                            Log.e("kit info", outKit.getKitMap().toString());
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(READ_FAILED, firebaseError.getMessage());
            }
        });

        switch (useCase) {
            case VIEW_PRODUCT_DETAILS:
                Firebase productsRef = database.child("products");

                productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (String prodId : outKit.getKitMap().keySet()) {
                            // look at products sub-database
                            DataSnapshot prodSnapshot = snapshot.child(prodId);

                            // product in kit not in database
                            if (!prodSnapshot.exists()) {
                                Log.e(READ_FAILED, "Product id " + prodId + "in kit " + kitName + " not found in products database");
                            }
                            else {
                                Product outProd = prodSnapshot.getValue(Product.class);
                                Log.d("Product info received", outProd.getName() + " " + StringCalendar.toString(outProd.getExpiry()));

                                /** TODO use product information in outProds to do something
                                 *  example store in array then display all in listview
                                 *  note that qty in outprod is total inventory quantity
                                 *  kit quantity is stored in outKit.getProduct(productId).getQuantity();
                                 **/
                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Log.e(READ_FAILED, firebaseError.getMessage());
                    }
                });

                break;
        }
    }


    // adds products to kit. DatabaseWriteKit.addProducts method
    // calls DatabaseReadKit.read, which finally calls this
    // the method should not be accessed outside of this class
    private static void writeAddProducts(Kit kit) {
        // locations where ProductInKits are stored
        Firebase ref = database.child("kits").child(kit.getKitName()).child("kitMap");

        // iterate through id-prodInKit pairs stored in kit
        for (Map.Entry<String, ProductInKit> entry : kit.getKitMap().entrySet())  {

            String id = entry.getKey();
            ProductInKit pink = entry.getValue();

            ref.child(id).setValue(pink);
        }
    }


    // the method should not be accessed outside of this class
    private static void delete(String kitName) {
        database.child("kits").child(kitName).removeValue();
    }
}
