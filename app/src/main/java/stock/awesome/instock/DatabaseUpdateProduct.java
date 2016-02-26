package stock.awesome.instock;


import com.firebase.client.Firebase;

/**
 * Constructor takes in either String id of product to be updated, or
 * Product with assigned id to be updated, as well as database to update to.
 */
public class DatabaseUpdateProduct {

    private Firebase database = null;
    private DatabaseReadProduct reader = null;

    public DatabaseUpdateProduct(Firebase database) {
        this.database = database;
    }


    // To update quantity of a product, pass in id and change in qty (pos/neg)
    public void updateQuantity(String id, int qtyChange) {
        updateHelper(null, id, qtyChange);
    }

    // To update all details of product, pass in product
    public void updateProduct(Product product) {
        updateHelper(product, null, 0);
    }


    private void updateHelper(Product product, String id, int qtyChange) {

        // only quantity should be increased/decreased
        if (product == null) {
            reader = new DatabaseReadProduct(database, DatabaseReadProduct.UseCase.UPDATE_QUANTITY_ONLY, qtyChange);
            reader.execute(id);
        }

        // rewrite all product information
        // IMPT: existing qty will not be increased/decreased but overwritten with product's quantity
        else {
            reader = new DatabaseReadProduct(database, DatabaseReadProduct.UseCase.UPDATE_PRODUCT, product);
            reader.execute(product.getId());
        }
    }
}
