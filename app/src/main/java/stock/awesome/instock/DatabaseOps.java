package stock.awesome.instock;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.FutureTask;


public class DatabaseOps {

    private Firebase database = null;
    private Product outProd = new Product();
    // outProd variables
//    String prodId = null, name = null, location = null;
//    int qty = -1;


    public DatabaseOps(Firebase database) {
        this.database = database;
    }


    private String format(GregorianCalendar calendar) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MMM/yyyy");
        fmt.setCalendar(calendar);
        String dateFormatted = fmt.format(calendar.getTime());
        return dateFormatted;
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
            newProd.put("expiry", format(product.getExpiry()));

        ref.setValue(newProd);
    }


    // NOT WORKING
    // returns a product with the name, quantity and location associated with id passed in
    // by looking up the id's characteristics in the database
    public Product readFromFirebase(final String id) {
        Firebase ref = database.child("products").child(id);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                FutureTask<String> name = new FutureTask<String>("5");
                (String) snapshot.child("name").getValue();
                String location = (String) snapshot.child("location").getValue();
                String strQty = (String) snapshot.child("quantity").getValue();

                outProd.setId(id);
                outProd.setName(name);
                outProd.setQuantity(Integer.parseInt(strQty));
                outProd.setLocation(location);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("Database read", "The read failed: " + firebaseError.getMessage());
            }
        });

        return outProd;
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


