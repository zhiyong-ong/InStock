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


/**
 * ** Always call execute with param kitName**
 * execute(String name) reads from database and returns a Kit associated with that name.
 */
public class DatabaseReadKit {
    private static final Firebase database = DatabaseLauncher.database;
    private static Kit outKit = new Kit();
    private static Exception e = null;

    public enum KitUseCase {
        UPDATE_KIT, DEBUG
    }


    // UNTESTED - change logging, insert more
    // returns a kit with the product associated with id passed in
    // by looking up the id's characteristics in the database
    public static Kit read(final String kitName, final KitUseCase useCase)
            throws ProductNotFoundException, KitNotFoundException, FirebaseException {

        outKit = new Kit();

        if (kitName == null) {
            throw new KitNotFoundException("No kit name given");
        }

        outKit.setKitName(kitName);

        Log.d("Adding listener ", "SingleValueEvent");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("onDataChange started ", "success");

                // kitName entered not in the database
                if (!snapshot.child("kits").child(kitName).exists()) {
                    e = new KitNotFoundException("Kit name: " + kitName + " not found in database");
                }

                // look at kits sub-database
                else {
                    for (DataSnapshot kitSnapshot : snapshot.child("kits").child(kitName).getChildren()) {

                        ProductInKit pink = kitSnapshot.getValue(ProductInKit.class);

                        String prodId = pink.getId();
                        int prodQty = pink.getQuantity();

                        Log.w("Kit info received", prodId + " " + Integer.toString(prodQty));

                        // look at products sub-database
                        DataSnapshot prodSnapshot = snapshot.child("products").child(prodId);

                        // product in kit not in database
                        if (!prodSnapshot.exists()) {
                            e = new ProductNotFoundException("Product id: " + prodId + " not found in products database");
                        } else {
                            Product outProd = prodSnapshot.getValue(Product.class);

                            Log.w("Product info received", outProd.getName() + " " + StringCalendar.toString(outProd.getExpiry()));

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
                throw new FirebaseException(firebaseError.getMessage());
            }
        });

        return outKit;
    }
}
