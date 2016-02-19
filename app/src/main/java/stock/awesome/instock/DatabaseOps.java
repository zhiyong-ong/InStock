package stock.awesome.instock;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class DatabaseOps {

    private Firebase database = null;
    private ArrayList<String> info = new ArrayList<String>();


    public DatabaseOps(Firebase database) {
        this.database = database;
    }


    private String format(GregorianCalendar calendar){
            SimpleDateFormat fmt = new SimpleDateFormat("dd/MMM/yyyy");
            fmt.setCalendar(calendar);
            String dateFormatted = fmt.format(calendar.getTime());
            return dateFormatted;
    }


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

    // returns an ArrayList of data related to the id passed in.
    // 0 index is name, 1 is quantity and 2 is location
    public ArrayList<String> readFromFirebase(String id) {
        Firebase ref = database.child("products").child(id);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                String name = (String) snapshot.child("name").getValue();
                String qty = (String) snapshot.child("quantity").getValue();
                String location = (String) snapshot.child("location").getValue();

                info.add(name);
                info.add(qty);
                info.add(location);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("Database read", "The read failed: " + firebaseError.getMessage());
            }
        });

        return info;
    }


}

