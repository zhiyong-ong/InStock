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
    private String useCase = null, id = null;


    public DatabaseReadKit(Firebase database, String useCase) {
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

        Firebase ref = database.child("products").child(kitName);
        Query queryRef = ref.orderByKey();

        final Semaphore semaphore = new Semaphore(0);
        Log.w("adding listener ", "listener");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Log.w("Stuff ", "hi");

                id = (String) snapshot.child("name").getValue();
                String qty = (String) snapshot.child("location").getValue();

                Log.w("Stuff has come back", id + " " + qty);

                Firebase prodRef = database.child("products").child(id);
                prodRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        Log.w("Stuff ", "hi");

                        String name = (String) snapshot.child("name").getValue();
                        String location = (String) snapshot.child("location").getValue();
                        String strQty = (String) snapshot.child("quantity").getValue();
                        String desc = (String) snapshot.child("description").getValue();
                        String strExpiry = (String) snapshot.child("expiry").getValue();

                        Log.w("Stuff has come back", name + " " + strQty);

                        outProd.setId(id);
                        outProd.setName(name);
                        outProd.setQuantity(Integer.parseInt(strQty));
                        outProd.setLocation(location);
                        outProd.setDesc(desc);
                        outProd.setExpiry(StringCalendar.toCalendar(strExpiry));

                        semaphore.release();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Log.e("Firebase read error", "The read failed: " + firebaseError.getMessage());
                        semaphore.release();
                    }
                });

                outKit.addProduct(outProd, Integer.parseInt(qty));

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

        if (useCase.equals("build_kit")) {
            // display name, location in BuildKitActivity
        }
    }
}

