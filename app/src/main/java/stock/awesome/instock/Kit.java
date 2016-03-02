package stock.awesome.instock;

import com.firebase.client.Firebase;

import java.util.LinkedHashMap;

// collection of products and associated quantities to be picked.
// Order input == order output.
// addProduct throws IllegalArgumentException if id is null
// TODO should it?
public class Kit {

    // key: product, value: qty of prod with that id
    private LinkedHashMap<Product, Integer> kit;
    private String kitName = null;
    // IMPT:
    private Firebase database = new Firebase("https://scorching-inferno-2190.firebaseio.com/");

    public Kit() {
        this(null);
    }

    public Kit(String kitName) {
        kit = new LinkedHashMap<Product, Integer>();
        this.kitName = kitName;
    }

    public String getKitName() {
        if (kitName == null) return "The kit has no name";
        else return kitName;
    }

    public void setKitName(String name) {
        kitName = name;
    }

    // adds as key value pair of prod:qty
    // throws IllegalArgumentException if product has no id
    public void addProduct(Product product, int qty) throws IllegalArgumentException {
        if (product.getId() == null) throw new IllegalArgumentException("Product has no id");
        else kit.put(product, qty);
    }

    // throws IllegalArgumentException if id is null
    public void addProduct(String id, int qty) throws IllegalArgumentException {
        Product newProd = new Product();
        newProd.setId(id);
        addProduct(newProd, qty);
    }

    // returns qty associated with id. if none, returns null
    public int getQty(String id) {
        return kit.get(id);
    }

    // removes id and associated qty
    public void removeProduct(String id) {
        kit.remove(id);
    }

    // get the HashMap that the kit is stored as. Useful for iterating over
    public LinkedHashMap<Product, Integer> getHashMap() {
        return kit;
    }
}
