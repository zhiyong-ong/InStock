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


public class DatabaseReadKit {
    private static final Firebase database = DatabaseLauncher.database;
    private static Kit outKit = new Kit();
    private static final String READ_FAILED = "Kit read failed";
    private static final String WRITE_FAILED = "Product write failed";

    public enum KitUseCase {
        UPDATE_KIT, VIEW_PRODUCT_DETAILS, DEBUG
    }


    // UNTESTED - change logging, insert more
    // returns a kit with the product associated with id passed in
    // by looking up the id's characteristics in the database
    public static void read(final String kitName, final KitUseCase useCase) {

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
                    DataSnapshot kitSnapshot = snapshot.child("kits").child(kitName);
                    Kit outKit = kitSnapshot.getValue(Kit.class);

                    switch (useCase) {
                        case VIEW_PRODUCT_DETAILS:
                            for (String prodId: outKit.getKit().keySet()) {
                                // look at products sub-database
                                DataSnapshot prodSnapshot = snapshot.child("products").child(prodId);

                                // product in kit not in database
                                if (!prodSnapshot.exists()) {
                                    Log.e(READ_FAILED, "Product id: " + prodId + "in kit " + kitName + " not found in products database");
                                }
                                else {
                                    Product outProd = prodSnapshot.getValue(Product.class);
                                    outProd.setQuantity(outKit.getProduct(prodId).getQuantity());
                                    Log.d("Product info received", outProd.getName() + " " + StringCalendar.toString(outProd.getExpiry()));

                                    /** TODO use product information in outProd to do something
                                     *  note that qty in outprod is that listed in kit
                                     **/
                                }
                            }
                            break;

                        case UPDATE_KIT:
                            // TODO
                            break;

                        case DEBUG:
                            Log.e("kit info", outKit.getKit().toString());
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
