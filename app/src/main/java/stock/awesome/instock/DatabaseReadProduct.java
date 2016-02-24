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

    private Firebase database = null;
    private Product outProd = new Product(), updatedProd = null;
    private String READ_FAILED = "Database read failed";
    private ProdUseCase useCase = null;
    private boolean readSuccess = true;
    private int qtyChange = 0;
    private DatabaseWriteProduct productWriter = null;

    public enum ProdUseCase {
        BUILD_KIT, UPDATE_PRODUCT, UPDATE_QUANTITY_ONLY
    }

    public DatabaseReadProduct(Firebase database, ProdUseCase useCase) {
        this(database, useCase, 0);
    }

    public DatabaseReadProduct(Firebase database, ProdUseCase useCase, int qtyChange) {
        this.database = database;
        this.useCase = useCase;
        this.qtyChange = qtyChange;
    }

    public DatabaseReadProduct(Firebase database, ProdUseCase useCase, Product updatedProd) {
        this.database = database;
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

        Firebase ref = database.child("products").child(id);

        final Semaphore semaphore = new Semaphore(0);
        Log.w("adding listener ", "listener");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.w("onDataChange started ", "success");

                // the product does not exist in the database
                if (!snapshot.exists()) {
                    Log.e(READ_FAILED, outProd.getId() + " not found"); // TODO display error msg
                    readSuccess = false;
                    return;
                }

                // if use case is to update product, no reading required.
                // only check needed is that item exists in database, which is handled above
                if (useCase.equals(ProdUseCase.UPDATE_PRODUCT)) {
                    return;
                }

                // if use case is to update quantity only, set outProd's qty to strQty.
                // Other operations performed in onPostExecute
                else if (useCase.equals(ProdUseCase.UPDATE_QUANTITY_ONLY)) {
                    String strQty = (String) snapshot.child("quantity").getValue();
                    outProd.setQuantity(Integer.parseInt(strQty));
                }

                // default behaviour
                else {
//                    String name = (String) snapshot.child("name").getValue();
//                    String location = (String) snapshot.child("location").getValue();
//                    String desc = (String) snapshot.child("description").getValue();
//                    String strQty = (String) snapshot.child("quantity").getValue();
//                    String strExpiry = (String) snapshot.child("expiry").getValue();
//
//                    Log.w("Stuff has come back", name + " " + strQty);
//
//                    outProd.setId(id);
//                    outProd.setName(name);
//                    outProd.setQuantity(Integer.parseInt(strQty));
//                    outProd.setLocation(location);
//                    outProd.setDesc(desc);
//                    outProd.setExpiry(StringCalendar.toCalendar(strExpiry));

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

    // TODO check: use outProd or result?
    @Override
    protected void onPostExecute(Product result) {
        if (readSuccess) {
            if (result.getName() != null) {
                Log.w("After Asynctask", result.getName());
            }

            switch (useCase) {
                case BUILD_KIT:
                    // TODO display name, location in BuildKitActivity
                    break;

                // rewrites all product info to database
                case UPDATE_PRODUCT:
                    productWriter = new DatabaseWriteProduct(database);
                    productWriter.writeProduct(updatedProd, ProdUseCase.UPDATE_PRODUCT);
                    break;

                // only updates qty
                case UPDATE_QUANTITY_ONLY:
                    int newQty = outProd.getQuantity() + qtyChange;
                    updatedProd.setQuantity(newQty);

                    productWriter = new DatabaseWriteProduct(database);
                    productWriter.writeProduct(updatedProd, ProdUseCase.UPDATE_QUANTITY_ONLY);
                    break;
            }
        }
    }

}

