package stock.awesome.instock;

import  java.util.HashMap;


public class Kit {

    // key: id, value: product with that id
    HashMap<String, Product> kit;

    public Kit() {
        kit = new HashMap<String, Product>();
    }

    // adds as key value pair of id:product
    public void addToKit(Product product) {
        kit.put(product.getId(), product);
    }

    // returns product associated with id. if none, returns null
    public Product getProductFromKit(String id) {
        return kit.get(id);
    }
}
