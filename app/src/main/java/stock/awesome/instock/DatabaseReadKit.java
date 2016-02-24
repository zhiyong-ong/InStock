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
 * execute(String name) reads from database and returns a Kit associated with that name.
 */
public class DatabaseReadKit extends AsyncTask<String, Void, Kit> {
    private Firebase database = null;
    private Kit outKit = new Kit();
    private Product outProd = new Product();
    private KitUseCase useCase = null;
    private String READ_FAILED = "Database read failed";
    private boolean readSuccess = true;

    public enum KitUseCase {
        SAVE_KIT, UPDATE_KIT
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

        // order kits by id
        Query queryRef = database.child("kits").child(kitName).orderByKey();

        final Semaphore semaphore = new Semaphore(0);
        Log.w("adding listener ", "listener");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.w("onDataChange started ", "success");

                // look at kits sub-database
                DataSnapshot kitSnapshot = snapshot.child("kits").child(kitName);

                // the product does not exist in the database
                if (!kitSnapshot.exists()) {
                    Log.e(READ_FAILED, outProd.getId() + " not found"); // TODO display error msg
                    readSuccess = false;
                    return;
                }

                String id = (String) kitSnapshot.child("name").getValue();
                String strQty = (String) kitSnapshot.child("location").getValue();

                Log.w("Kit info received", id + " " + strQty);

                // look at products sub-database
                DataSnapshot prodSnapshot = snapshot.child("products").child(id);

                String name = (String) prodSnapshot.child("name").getValue();
                String location = (String) prodSnapshot.child("location").getValue();
                String desc = (String) prodSnapshot.child("description").getValue();
                String strExpiry = (String) prodSnapshot.child("expiry").getValue();

                Log.w("Product info received", name + " " + strExpiry);

                // variables from kit sub-db
                outProd.setId(id);
                outProd.setQuantity(Integer.parseInt(strQty));
                // variables from products sub-db
                outProd.setName(name);
                outProd.setLocation(location);
                outProd.setDesc(desc);
                outProd.setExpiry(StringCalendar.toCalendar(strExpiry));

                outKit.addProduct(outProd, Integer.parseInt(strQty));
                semaphore.release();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("Firebase read error", "The read failed: " + firebaseError.getMessage());
                semaphore.release();
            }
        });

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Log.e("Semaphore acqn failed", e.getMessage());
            semaphore.release();
        }

        return outKit;
    }

    @Override
    protected void onPostExecute(Kit result) {
        // Log.w("After Asynctask", result.getName());
        if (readSuccess) {
            switch (useCase) {
                case SAVE_KIT:
                    break;

                case UPDATE_KIT:
                    break;
            }
        }
    }
}

