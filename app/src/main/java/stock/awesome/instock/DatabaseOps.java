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


public class DatabaseOps extends AsyncTask<String, Void, Product>{

    private Firebase database = null;
    private Product outProd = new Product();
    // outProd variables
//    String prodId = null, name = null, location = null;
//    int qty = -1;


    public DatabaseOps(Firebase database) {
        this.database = database;
    }

    // static because used in inputstockactivity
    public static String formatCalendarAsString(GregorianCalendar calendar) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
        fmt.setCalendar(calendar);
        return fmt.format(calendar.getTime());
    }


    // writes all the characteristic data of a product to database.
    // must have id, other values optional. All string fields are initialised with null values
    // and integer fields with -1 except expiry. If expiry date is null,
    // that field is not created
    public void writeToFirebase(Product product) {
        Firebase ref = database.child("products").child(product.getId());
        Map<String, String> newProd = new HashMap<String, String>();

        newProd.put("name", product.getName());
        newProd.put("desc", product.getDesc());
        newProd.put("location", product.getLocation());
        newProd.put("quantity", Integer.toString(product.getQuantity()));
        if (product.getExpiry() != null)
            newProd.put("expiry", formatCalendarAsString(product.getExpiry()));

        ref.setValue(newProd);
    }


    // NOT WORKING
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
        // display text
    }

}
//    // NOT WORKING
//    // returns a product with the name, quantity and location associated with id passed in
//    // by looking up the id's characteristics in the database
//    public Product readFromFirebase(String id) {
//        readHelper(id, new Runnable() {
//            public void run() {
//                outProd.setId(prodId);
//                outProd.setName(name);
//                outProd.setQuantity(qty);
//                outProd.setLocation(location);
//            }
//        });
//
//        return outProd;
//    }
//
//
//    public void readHelper(String id, final Runnable onLoaded) {
//
//        Firebase ref = database.child("products").child(id);
//
//        prodId = id;
//
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//
//                name = (String) snapshot.child("name").getValue();
//                location = (String) snapshot.child("location").getValue();
//                String strQty = (String) snapshot.child("quantity").getValue();
//                qty = Integer.parseInt(strQty);
//
//                onLoaded.run();
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//                Log.e("Database read", "The read failed: " + firebaseError.getMessage());
//            }
//        });
//    }


