package stock.awesome.instock;

import java.util.GregorianCalendar;

public class Product {

    private String id = null, name = null, desc = null, location = null;
    private int quantity = -1;
    private GregorianCalendar expiry = null;


    public Product() {}

    public Product(String id, String name) {
        this(id, name, null, null, -1, null);
    }

    public Product(String id, String name, String desc, String location, int quantity, GregorianCalendar expiry) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.location = location;
        this.quantity = quantity;
        this.expiry = expiry;
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
