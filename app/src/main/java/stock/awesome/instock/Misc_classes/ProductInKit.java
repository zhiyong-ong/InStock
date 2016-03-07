package stock.awesome.instock.misc_classes;


import java.io.Serializable;

// Only contains id and qty. Needed for firebase read.
// For other uses, see Product.
public class ProductInKit {

    private String id = null;
    private int quantity = -1;

    public ProductInKit() {
    }

    public ProductInKit(String id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}