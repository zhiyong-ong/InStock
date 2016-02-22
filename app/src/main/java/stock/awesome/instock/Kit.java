package stock.awesome.instock;

import com.firebase.client.Firebase;

import  java.util.HashMap;


public class Kit {

    // key: id, value: product with that id
    private HashMap<String, Integer> kit;
    private String kitName = null;
    // IMPT:
    private Firebase database = new Firebase("https://scorching-inferno-2190.firebaseio.com/");

    public Kit() {
        this(null);
    }

    public Kit(String kitName) {
        kit = new HashMap<String, Integer>();
        this.kitName = kitName;
    }

    public String getKitName() {
        return kitName;
    }

    // adds as key value pair of id:qty
    public void addProduct(Product product, int qty) {
        kit.put(product.getId(), qty);
    }

    public void addProduct(String id, int qty) {
        kit.put(id, qty);
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
    public HashMap<String, Integer> getHashMap() {
        return kit;
    }
}
