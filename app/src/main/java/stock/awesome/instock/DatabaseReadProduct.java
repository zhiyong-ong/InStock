package stock.awesome.instock;

import android.os.AsyncTask;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;

/**
 * writeToFirebase(Product product) writes to database.
 * execute(String id) reads from database and returns a Product.
 */
public class DatabaseReadProduct extends AsyncTask<String, Void, Product>{

    private Firebase database = null;
    private Product outProd = new Product();
    private String useCase = null;


    public DatabaseReadProduct(Firebase database, String useCase) {
        this.database = database;
        this.useCase = useCase;
    }


    // returns a product with the name, quantity and location associated with id passed in
    // by looking up the id's characteristics in the database
    @Override
    protected Product doInBackground(String... params) {
        final String id = params[0];
        Firebase ref = database.child("products").child(id);

        final Semaphore semaphore = new Semaphore(0);
        Log.w("adding listener ", "listener");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Log.w("Stuff ", "hi" );

                String name = (String) snapshot.child("name").getValue();
                String location = (String) snapshot.child("location").getValue();
                String strQty = (String) snapshot.child("quantity").getValue();

                Log.w("Stuff has come back", name + " " + strQty);

                outProd.setId(id);
                outProd.setName(name);
                outProd.setQuantity(Integer.parseInt(strQty));
                outProd.setLocation(location);

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
        }
        catch (InterruptedException e) {
            Log.e("Semaphore acqn failed", e.getMessage());
            semaphore.release();
        }

        return outProd;
    }

    @Override
    protected void onPostExecute(Product result) {
        Log.w("After Asynctask", result.getName());

        if (useCase.equals("build_kit")) {
            // display in editText
        }
    }

}

