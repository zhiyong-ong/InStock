package stock.awesome.instock;

import  java.util.HashMap;


public class Kit {

    // key: id, value: product with that id
    private HashMap<String, Product> kit;

    public Kit() {
        kit = new HashMap<String, Product>();
    }

    // adds as key value pair of id:product
    public void addProduct(Product product) {
        kit.put(product.getId(), product);
    }

    // returns product associated with id. if none, returns null
    public Product getProduct(String id) {
        return kit.get(id);
    }

    // removes product associate with id argument
    public void removeProduct(String id) {
        kit.remove(id);
    }
}
