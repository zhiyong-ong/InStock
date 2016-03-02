package stock.awesome.instock;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.ValueEventListener;

import stock.awesome.instock.misc_classes.Kit;
import stock.awesome.instock.misc_classes.Product;
import stock.awesome.instock.misc_classes.ProductInKit;
import stock.awesome.instock.misc_classes.StringCalendar;
import stock.awesome.instock.exceptions.KitNotFoundException;
import stock.awesome.instock.exceptions.ProductNotFoundException;


public class DatabaseReadKit {
    private static final Firebase database = DatabaseLauncher.database;
    private static Kit outKit = new Kit();
    private static final String READ_FAILED = "Kit read failed";
    private static final String WRITE_FAILED = "Product write failed";

    public enum KitUseCase {
        UPDATE_KIT, DEBUG
    }


    // UNTESTED - change logging, insert more
    // returns a kit with the product associated with id passed in
    // by looking up the id's characteristics in the database
    public static void read(final String kitName, final KitUseCase useCase, final Product updatedProd) {

        outKit = new Kit();
        outKit.setKitName(kitName);

        Log.d("Adding listener ", "SingleValueEvent");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("onDataChange started", "success");

                // kitName entered not in the database
                if (!snapshot.child("kits").child(kitName).exists()) {
                    Log.e(READ_FAILED, "Kit name: " + kitName + " not found in database");
                }

                // look at kits sub-database
                else {
                    for (DataSnapshot kitSnapshot : snapshot.child("kits").child(kitName).getChildren()) {

                        ProductInKit pink = kitSnapshot.getValue(ProductInKit.class);

                        String prodId = pink.getId();
                        int prodQty = pink.getQuantity();

                        Log.d("Kit info received", prodId + " " + Integer.toString(prodQty));

                        // look at products sub-database
                        DataSnapshot prodSnapshot = snapshot.child("products").child(prodId);

                        // product in kit not in database
                        if (!prodSnapshot.exists()) {
                            Log.e(READ_FAILED, "Product id: " + prodId + "in kit " + kitName + " not found in products database");
                        }
                        else {
                            Product outProd = prodSnapshot.getValue(Product.class);

                            Log.d("Product info received", outProd.getName() + " " + StringCalendar.toString(outProd.getExpiry()));

                            // variables from kit sub-db
                            outProd.setId(prodId);
                            outProd.setQuantity(prodQty);

                            outKit.addProduct(outProd, prodQty);

                            switch (useCase) {
                                case UPDATE_KIT:
                                    // TODO
                                    break;

                                case DEBUG:
                                    Log.w("Kit info", outKit.getHashMap().toString());
                            }
                        }
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
