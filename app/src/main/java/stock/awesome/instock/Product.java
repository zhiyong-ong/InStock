package stock.awesome.instock;

import android.util.Log;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class Product {

    private String id = null, name = null, desc = null, location = null;
    private int quantity = -1;
    private GregorianCalendar expiry = null;
    private boolean success = false;


    public Product(String id, String name) {
        this.id = id;
        this.name = name;
    }


    private String format(GregorianCalendar calendar){
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MMM/yyyy");
        fmt.setCalendar(calendar);
        String dateFormatted = fmt.format(calendar.getTime());
        return dateFormatted;
    }


    public void writeToFirebase(Firebase database) {
        Firebase ref = database.child("products").child(id);
        Map<String, String> newProd = new HashMap<String, String>();

        newProd.put("name", name);
        newProd.put("desc", desc);
        newProd.put("location", location);
        newProd.put("quantity", Integer.toString(quantity));
        newProd.put("location", location);
        if (expiry != null)
            newProd.put("expiry", format(expiry));

        ref.setValue(newProd);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public GregorianCalendar getExpiry() {
        return expiry;
    }

    public void setExpiry(GregorianCalendar expiry) {
        this.expiry = expiry;
    }
}
