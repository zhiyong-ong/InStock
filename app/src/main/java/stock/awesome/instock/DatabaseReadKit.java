package stock.awesome.instock;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import stock.awesome.instock.misc_classes.Kit;
import stock.awesome.instock.misc_classes.KitAdapter;
import stock.awesome.instock.misc_classes.Product;
import stock.awesome.instock.misc_classes.ProductInKit;
import stock.awesome.instock.misc_classes.StringCalendar;


public class DatabaseReadKit {
    private static final Firebase database = DatabaseLauncher.database;
    private static Kit outKit = new Kit();
    private static Context fromContext;
    private static Class toClass;
    private static final String READ_FAILED = "Kit read failed";

    public enum KitUseCase {
        UPDATE_KIT, GET_PRODUCT_DETAILS, DEBUG, DELETE_KIT
    }


    // useCase GET_PRODUCT_DETAILS
    public static void read(@NotNull String kitName, @NotNull KitUseCase useCase,
                            @NotNull Context thisContext, @NotNull Class nextClass) throws IllegalArgumentException {
        if ( !(useCase.equals(KitUseCase.GET_PRODUCT_DETAILS)) ) {
            throw new IllegalArgumentException("useCase must be GET_PRODUCT_DETAILS");
        }

        fromContext = thisContext;
        toClass = nextClass;
        Kit emptyKit = new Kit(kitName);
        read(kitName, useCase, emptyKit);
    }

    // useCase DEBUG, DELETE_KIT
    public static void read(@NotNull String kitName, @NotNull KitUseCase useCase) throws IllegalArgumentException {
        if ( !(useCase.equals(KitUseCase.DEBUG) || useCase.equals(KitUseCase.DELETE_KIT)) ) {
            throw new IllegalArgumentException("useCase must be DELETE_KIT or DEBUG");
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

    // useCase GET_PRODUCT_DETAILS, DEBUG
    // returns a kit with the product associated with id passed in
    // by looking up the id's characteristics in the database
    private static void read(@NotNull final String kitName, @NotNull final KitUseCase useCase, @NotNull final Kit updatedKit) {

        outKit = new Kit();

        Firebase ref = database.child("kits").child(kitName);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

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

                        case GET_PRODUCT_DETAILS:
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
            case GET_PRODUCT_DETAILS:

                Firebase productsRef = database.child("products");

                productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        HashMap<String, Product> prodMap = new HashMap<String, Product>();

                        for (String prodId : outKit.getKitMap().keySet()) {
                            // look at products sub-database
                            DataSnapshot prodSnapshot = snapshot.child(prodId);

                            // product in kit not in database
                            if (!prodSnapshot.exists()) {
                                Log.e(READ_FAILED, "Product ID " + prodId + " in kit " + kitName + " not found in products database");
                            }
                            else {
                                Product outProd = prodSnapshot.getValue(Product.class);
                                Log.d("Product info received", outProd.getName() + " " + StringCalendar.toString(outProd.getExpiry()));

                                prodMap.put(outProd.getId(), outProd);
                            }
                        }

                        // send hashmap to KitAdapter so it can display product's other info (eg location)
                        KitAdapter.getProductDetails(prodMap);

                        Intent intent = new Intent(fromContext, toClass);
                        fromContext.startActivity(intent);
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
