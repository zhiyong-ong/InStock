package stock.awesome.instock;

import android.os.AsyncTask;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.concurrent.Semaphore;


/**
 * ** Always call execute with param kitName**
 * execute(String name) reads from database and returns a Kit associated with that name.
 */
public class DatabaseReadKit extends AsyncTask<String, Void, Kit> {
    private Firebase database = null;
    private Kit outKit = new Kit();
    private KitUseCase useCase = null;
    private String READ_FAILED = "Kit database read failed";
    private boolean readSuccess = true;

    public enum KitUseCase {
        UPDATE_KIT, DEBUG
    }

    public DatabaseReadKit(Firebase database, KitUseCase useCase) {
        this.database = database;
        this.useCase = useCase;
    }


    // UNTESTED - change logging, insert more
    // returns a kit with the product associated with id passed in
    // by looking up the id's characteristics in the database
    @Override
    protected Kit doInBackground(String... params) {
        final String kitName = params[0];
        outKit.setKitName(kitName);

        final Semaphore semaphore = new Semaphore(0);
        Log.w("Adding listener ", "SingleValueEvent");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.w("onDataChange started ", "success");

                if (!snapshot.child("kits").child(kitName).exists()) {
                    Log.e(READ_FAILED, kitName + " not found"); // TODO display error msg
                    readSuccess = false;
                    return;
                }

                // look at kits sub-database
                for (DataSnapshot kitSnapshot: snapshot.child("kits").child(kitName).getChildren()) {

                    ProductInKit pink = kitSnapshot.getValue(ProductInKit.class);

                    String prodId = pink.getId();
                    int prodQty = pink.getQuantity();

                    Log.w("Kit info received", prodId + " " + Integer.toString(prodQty));

                    // look at products sub-database
                    DataSnapshot prodSnapshot = snapshot.child("products").child(prodId);

                    if (!prodSnapshot.exists()) {
                        Log.e(READ_FAILED, prodId + " not found in products database"); // TODO display error msg
                        readSuccess = false;
                        return;
                    }

                    Product outProd = prodSnapshot.getValue(Product.class);

                    Log.w("Product info received", outProd.getName() + " " + StringCalendar.toString(outProd.getExpiry()));
//
                    // variables from kit sub-db
                    outProd.setId(prodId);
                    outProd.setQuantity(prodQty);

                    outKit.addProduct(outProd, prodQty);
                }

                semaphore.release();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                semaphore.release();
                Log.e(READ_FAILED, "Firebase read error: " + firebaseError.getMessage()); // TODO display error msg
                readSuccess = false;
            }
        });

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            semaphore.release();
            Log.e(READ_FAILED, "Semaphore acqn failed: " + e.getMessage()); // TODO display error msg
            readSuccess = false;
        }

        return outKit;
    }

    @Override
    protected void onPostExecute(Kit result) {
        // Log.w("After Asynctask", result.getName());
        if (readSuccess) {
            switch (useCase) {
                case UPDATE_KIT:
                    break;

                case DEBUG:
                    Log.w("Kit info:", result.getHashMap().toString());
            }
        }
    }
}

