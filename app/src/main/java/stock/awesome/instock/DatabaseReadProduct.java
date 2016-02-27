package stock.awesome.instock;

import android.os.AsyncTask;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.concurrent.Semaphore;

/**
 * ** Always call execute with param id **
 * execute(String id) reads from database and returns a Product associated with that id.
 */
public class DatabaseReadProduct extends AsyncTask<String, Void, Product> {

    private final static Firebase database = DatabaseLauncher.database;
    private Product outProd = new Product(), updatedProd = null;
    private String READ_FAILED = "Product database read failed";
    private ProdUseCase useCase = null;
    private boolean readSuccess = true;
    private int qtyChange = 0;
    private DatabaseWriteProduct productWriter = null;

    public enum ProdUseCase {
        BUILD_KIT, UPDATE_PRODUCT, UPDATE_QUANTITY_ONLY, VIEW_ALL_STOCKS, DEBUG
    }

    public DatabaseReadProduct(ProdUseCase useCase) {
        this(useCase, 0);
    }

    public DatabaseReadProduct(ProdUseCase useCase, int qtyChange) {
        this.useCase = useCase;
        this.qtyChange = qtyChange;
    }

    public DatabaseReadProduct(ProdUseCase useCase, Product updatedProd) {
        this.useCase = useCase;
        this.updatedProd = updatedProd;
    }


    // returns a product with the name, quantity and location associated with id passed in
    // by looking up the id's characteristics in the database
    @Override
    protected Product doInBackground(String... params) {
        final String id = params[0];
        if (id == null) {
            Log.e(READ_FAILED, "No product ID given"); // TODO display error msg
            readSuccess = false;
            return outProd;
        }

        Firebase ref = database.child("products");

        final Semaphore semaphore = new Semaphore(0);
        Log.w("adding listener ", "listener");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot bigSnapshot) {
                Log.w("onDataChange started ", "success");

                DataSnapshot snapshot = bigSnapshot.child(id);

                // the product does not exist in the database
                if (!snapshot.exists()) {
                    Log.e(READ_FAILED, outProd.getId() + " not found"); // TODO display error msg
                    readSuccess = false;
                    return;
                }

                // TODO
                if (useCase.equals(ProdUseCase.VIEW_ALL_STOCKS)) {
//                    for (DataSnapshot kitSnapshot: snapshot.getChildren()) {}
                }

                // if use case is to update product, no reading required.
                // only check needed is that item exists in database, which is handled above
                else if (useCase.equals(ProdUseCase.UPDATE_PRODUCT)) {
                }

                // if use case is to update quantity only, set outProd's qty to qty.
                // Other operations performed in onPostExecute
                else if (useCase.equals(ProdUseCase.UPDATE_QUANTITY_ONLY)) {
                    int qty = (int) snapshot.child("quantity").getValue();
                    outProd.setQuantity(qty);
                }

                // default behaviour
                else {
                    outProd = snapshot.getValue(Product.class);
                }

                semaphore.release();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                semaphore.release();
                Log.e(READ_FAILED, "Firebase read error: " + firebaseError.getMessage()); // TODO display error msg
                readSuccess = false;
            }
        });

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            semaphore.release();
            Log.e(READ_FAILED, "Semaphore acqn failed: " + e.getMessage()); // TODO display error msg
            readSuccess = false;
        }

        return outProd;
    }

    // Both result and outProd can be used
    @Override
    protected void onPostExecute(Product result) {
        if (readSuccess) {
            Log.w("onPostExecute", "success");

            switch (useCase) {
                case BUILD_KIT:
                    //BuildKitActivity.displayProduct(result);
                    break;

                // rewrites all product info to database
                case UPDATE_PRODUCT:
                    productWriter = new DatabaseWriteProduct();
                    productWriter.writeProduct(updatedProd, ProdUseCase.UPDATE_PRODUCT);
                    break;

                // only updates qty
                case UPDATE_QUANTITY_ONLY:
                    int newQty = outProd.getQuantity() + qtyChange;
                    updatedProd.setQuantity(newQty);

                    productWriter = new DatabaseWriteProduct();
                    productWriter.writeProduct(updatedProd, ProdUseCase.UPDATE_QUANTITY_ONLY);
                    break;

                case DEBUG:
                    Log.w("result info", result.getId() + " " + result.getName() + " " +
                            result.getQuantity() + " " + StringCalendar.toString(result.getExpiry()));
                    break;
            }
        }
    }

}

