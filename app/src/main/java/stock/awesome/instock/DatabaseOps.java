package stock.awesome.instock;

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


public class DatabaseOps {

    private Firebase database = null;
    //private Product outProd = new Product();
    // outProd variables
//    String prodId = null, name = null, location = null;
//    int qty = -1;


    public DatabaseOps(Firebase database) {
        this.database = database;
    }


    private String formatCalendarAsString(GregorianCalendar calendar) {
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
    public void readFromFirebase(final Product outProd, final String id) {
        Firebase ref = database.child("products").child(id);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                DbCallable nameCallable = new DbCallable(snapshot, "name");
                FutureTask<String> futureName = new FutureTask<String>(nameCallable);

                DbCallable locationCallable = new DbCallable(snapshot, "location");
                FutureTask<String> futureLocation = new FutureTask<String>(locationCallable);

                DbCallable qtyCallable = new DbCallable(snapshot, "quantity");
                FutureTask<String> futureQty = new FutureTask<String>(qtyCallable);

                ExecutorService executor = Executors.newFixedThreadPool(3);
                executor.execute(futureName);
                executor.execute(futureLocation);
                executor.execute(futureQty);

                outProd.setId(id);

                try {
                    outProd.setName(futureName.get());
                    outProd.setQuantity(Integer.parseInt(futureQty.get()));
                    outProd.setLocation(futureLocation.get());
                }
                catch (InterruptedException | ExecutionException e) {
                    Log.e("Future error", "Interrupted/ExecutionException");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("Firebase read error", "The read failed: " + firebaseError.getMessage());
            }
        });
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


