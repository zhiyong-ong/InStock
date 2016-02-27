//package stock.awesome.instock;
//
//
//import com.firebase.client.Firebase;
//
//import stock.awesome.instock.exceptions.ProductNotFoundException;
//
///**
// * Constructor takes in either String id of product to be updated, or
// * Product with assigned id to be updated, as well as database to update to.
// */
//public class DatabaseUpdateProduct {
//
//    private static final Firebase database = DatabaseLauncher.database;
//
//    // To update quantity of a product, pass in id and change in qty (pos/neg)
//    public static void updateQuantity(String id, int qtyChange) throws ProductNotFoundException {
//        try {
//            DatabaseReadProduct.read(id, DatabaseReadProduct.ProdUseCase.UPDATE_QUANTITY_ONLY, qtyChange);
//        } catch (ProductNotFoundException e) {
//            throw new ProductNotFoundException(e.getMessage());
//        }
//    }
//
//    // rewrite all product information
//    // IMPT: existing qty will not be increased/decreased but overwritten with product's quantity
//    public static void updateProduct(Product product) throws ProductNotFoundException {
//        try {
//            DatabaseReadProduct.read(product.getId(), DatabaseReadProduct.ProdUseCase.UPDATE_PRODUCT, product);
//        } catch (ProductNotFoundException e) {
//            throw new ProductNotFoundException(e.getMessage());
//        }
//    }
//
//}
