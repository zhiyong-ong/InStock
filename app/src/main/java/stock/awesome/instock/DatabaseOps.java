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

public class DatabaseOps {

    private Firebase database = null;
    private Product product = new Product();


    public DatabaseOps(Firebase database) {
        this.database = database;
    }


    private String format(GregorianCalendar calendar){
            SimpleDateFormat fmt = new SimpleDateFormat("dd/MMM/yyyy");
            fmt.setCalendar(calendar);
            String dateFormatted = fmt.format(calendar.getTime());
            return dateFormatted;
    }

    // writes all the characteristic data of a product to database
    // must have id, other values optional. if expiry date is null, that
    // field is not created
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
    public Product readFromFirebase(String id) {
        Firebase ref = database.child("products").child(id);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                String name = (String) snapshot.child("name").getValue();
                String qty = (String) snapshot.child("quantity").getValue();
                String location = (String) snapshot.child("location").getValue();

                product.setName(name);
                product.setQuantity(Integer.parseInt(qty));
                product.setLocation(location);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("Database read", "The read failed: " + firebaseError.getMessage());
            }
        });

        return product;
    }


}

